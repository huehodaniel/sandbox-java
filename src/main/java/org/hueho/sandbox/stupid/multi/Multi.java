package org.hueho.sandbox.stupid.multi;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.hueho.sandbox.stupid.multi.annotations.Multimethod;
import org.hueho.sandbox.stupid.multi.annotations.MultimethodFor;
import org.hueho.sandbox.stupid.multi.dispatchers.SlowDispatcher;
import org.hueho.sandbox.stupid.multi.exceptions.MultiException;
import org.hueho.sandbox.stupid.util.ClassToClassMap;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class Multi {
    private static final Multi _GLOBAL = new Multi();

    private final ClassLoader classLoader;
    private final ClassToClassMap classCache;

    private Multi() {
        this(ClassLoader.getSystemClassLoader());
    }

    public Multi(ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.classCache = new ClassToClassMap();
    }

    public static <T> T get(Class<T> targetClass) {
        return _GLOBAL.create(targetClass);
    }

    public <T> T create(Class<T> targetClass) {
        Class<? extends T> enhancedClass = getOrCreateMultiClass(targetClass);

        return newInstanceOf(enhancedClass);
    }

    private <T> T newInstanceOf(Class<? extends T> enhancedClass) {
        try {
            var constructor = enhancedClass.getConstructor();
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> Class<? extends T> getOrCreateMultiClass(Class<T> targetClass) {
        var enhancedClass = classCache.get(targetClass);
        if(enhancedClass != null) return enhancedClass;

        if(targetClass.getSuperclass().equals(Object.class)) {
            var methods = targetClass.getDeclaredMethods();
            var multiMethods = Arrays.stream(methods)
                    .filter(m -> m.getDeclaredAnnotation(Multimethod.class) != null).toList();

            if(multiMethods.isEmpty()) {
                throw new MultiException("No multimethods found! Annotate your entry points with @Multimethod");
            }

            var builder = new ByteBuddy().subclass(targetClass);

            for (Method multiMethod : multiMethods) {
                var name = multiMethod.getName();

                var candidates = Arrays.stream(methods).filter(m -> Optional
                        .ofNullable(m.getDeclaredAnnotation(MultimethodFor.class))
                        .map(ann -> ann.value().equals(name))
                        .orElse(false)).toList();

                if(candidates.isEmpty()) {
                    throw new MultiException("No multimethods candidates found for method [" + name + "]! Annotate your candidates with @MultimethodFor");
                }
                var illegalMethods = candidates.stream().filter(m -> m.getParameterCount() != multiMethod.getParameterCount()).toList();

                if(!illegalMethods.isEmpty()) {
                    var methodList = "[" +
                            illegalMethods.stream().map(m -> m.getName() + ":" + m.getParameterCount()).collect(Collectors.joining(", ")) + "]";
                    throw new MultiException("Some candidates for multimethod [" + name + "] are invalid! Methods with parameter counts are: " + methodList);
                }

                Dispatcher dispatcher = new SlowDispatcher(multiMethod, candidates);
                builder = builder
                        .define(multiMethod).intercept(
                                MethodDelegation
                                        .withDefaultConfiguration()
                                        .filter(ElementMatchers.named(Dispatcher.INVOKE))
                                        .to(dispatcher));

            }

            enhancedClass = builder.make().load(classLoader).getLoaded();
            classCache.put(targetClass, enhancedClass);
        } else {
            throw new MultiException("Not implemented yet");
        }

        return enhancedClass;
    }
}
