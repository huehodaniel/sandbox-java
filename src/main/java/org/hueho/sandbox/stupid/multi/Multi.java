package org.hueho.sandbox.stupid.multi;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.hueho.sandbox.stupid.multi.annotations.Multimethod;
import org.hueho.sandbox.stupid.multi.annotations.MultimethodFor;
import org.hueho.sandbox.stupid.multi.dispatchers.SlowDispatcherBuilder;
import org.hueho.sandbox.stupid.multi.exceptions.MultiException;
import org.hueho.sandbox.stupid.util.ClassToClassMap;

import java.lang.reflect.Method;
import java.util.*;
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

    @SuppressWarnings("unchecked")
    private <T> Class<? extends T> getOrCreateMultiClass(Class<T> targetClass) {
        var enhancedClass = classCache.get(targetClass);
        if (enhancedClass != null) return enhancedClass;

        var methods = targetClass.getMethods();
        var multiMethods = Arrays.stream(methods)
                .filter(m -> m.getAnnotation(Multimethod.class) != null).toList();

        if (multiMethods.isEmpty()) {
            throw new MultiException("No multimethods found! Annotate your entry points with @Multimethod");
        }

        Set<Class<?>> declarants = new HashSet<>();
        Class<?> currentClass = targetClass.getSuperclass();
        while (currentClass != Object.class) {
            declarants.add(currentClass);
            currentClass = currentClass.getSuperclass();
        }

        DynamicType.Unloaded<? extends T> unloadedType;
        if (declarants.isEmpty()) {
            unloadedType = buildType(targetClass, multiMethods);
        } else {
            unloadedType = buildComplexType(targetClass, multiMethods, declarants);
        }

        enhancedClass = unloadedType.load(classLoader).getLoaded();
        classCache.put(targetClass, enhancedClass);
        return enhancedClass;
    }

    private <T> DynamicType.Unloaded<? extends T> buildType(Class<T> baseClass, List<Method> multiMethods) {
        var builder = new ByteBuddy().subclass(baseClass);

        var methods = baseClass.getDeclaredMethods();

        for (Method multiMethod : multiMethods) {
            var name = multiMethod.getName();

            var candidates = Arrays.stream(methods).filter(m -> Optional
                    .ofNullable(m.getDeclaredAnnotation(MultimethodFor.class))
                    .map(ann -> ann.value().equals(name))
                    .orElse(false)).toList();

            if (candidates.isEmpty()) {
                throw new MultiException("No multimethods candidates found for method [" + name + "]! Annotate your candidates with @MultimethodFor");
            }
            var illegalMethods = candidates.stream().filter(m -> m.getParameterCount() != multiMethod.getParameterCount()).toList();

            if (!illegalMethods.isEmpty()) {
                var methodList = "[" +
                        illegalMethods.stream().map(m -> m.getName() + ":" + m.getParameterCount()).collect(Collectors.joining(", ")) + "]";
                throw new MultiException("Some candidates for multimethod [" + name + "] are invalid! Methods with parameter counts are: " + methodList);
            }

            builder = setDispatchForMultimethod(builder, multiMethod, candidates);
        }

        return builder.make();
    }

    private <T> DynamicType.Unloaded<? extends T> buildComplexType(Class<T> baseClass, List<Method> multiMethods, Set<Class<?>> declarants) {
        var builder = new ByteBuddy().subclass(baseClass);
        declarants.add(baseClass);

        Map<Method, List<Method>> multiMethodsToDispatch = new HashMap<>();

        for (Class<?> declarant : declarants) {
            var methods = declarant.getDeclaredMethods();

            for (Method multiMethod : multiMethods) {
                var currentCandidates = multiMethodsToDispatch
                        .computeIfAbsent(multiMethod, (_m) -> new ArrayList<>());

                var name = multiMethod.getName();

                var candidates = Arrays.stream(methods).filter(m -> Optional
                        .ofNullable(m.getDeclaredAnnotation(MultimethodFor.class))
                        .map(ann -> ann.value().equals(name))
                        .orElse(false)).toList();

                var illegalMethods = candidates.stream().filter(m -> m.getParameterCount() != multiMethod.getParameterCount()).toList();

                if (!illegalMethods.isEmpty()) {
                    var methodList = "[" +
                            illegalMethods.stream().map(m -> m.getName() + ":" + m.getParameterCount()).collect(Collectors.joining(", ")) + "]";
                    throw new MultiException("Some candidates for multimethod [" + name + "] are invalid! Methods with parameter counts are: " + methodList);
                }

                currentCandidates.addAll(candidates);
            }
        }

        for (var entry : multiMethodsToDispatch.entrySet()) {
            var multiMethod = entry.getKey();
            var candidates = entry.getValue();

            if (candidates.isEmpty()) {
                throw new MultiException("No multimethods candidates found for method [" + multiMethod.getName() +
                        "]! Annotate your candidates with @MultimethodFor");
            }

            builder = setDispatchForMultimethod(builder, multiMethod, candidates);
        }

        return builder.make();
    }

    private <T> DynamicType.Builder<T> setDispatchForMultimethod(DynamicType.Builder<T> builder, Method multiMethod, List<Method> candidates) {
        SlowDispatcherBuilder slowDispatcherBuilder = new SlowDispatcherBuilder(multiMethod);

        for (Method candidate : candidates) {
            MultimethodFor annotation = candidate.getDeclaredAnnotation(MultimethodFor.class);

            slowDispatcherBuilder.add(candidate);
            if (annotation.deriveMirror()) {
                slowDispatcherBuilder.addMirrored(candidate);
            }
        }

        builder = builder
                .define(multiMethod).intercept(
                        MethodDelegation
                                .withDefaultConfiguration()
                                .filter(ElementMatchers.named(Dispatcher.INVOKE))
                                .to(slowDispatcherBuilder.build()));
        return builder;
    }
}
