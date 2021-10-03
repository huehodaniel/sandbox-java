package org.hueho.sandbox.stupid.multi.dispatchers;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import org.hueho.sandbox.stupid.multi.Dispatcher;
import org.hueho.sandbox.stupid.multi.exceptions.MultiException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SlowDispatcher implements Dispatcher {
    private final Method toDispatch;
    private final Optional<Method> defaultDispatch;
    private final List<DispatchTuple> dispatchTuples;

    public SlowDispatcher(Method toDispatch, List<Method> candidates) {
        this(toDispatch, null, candidates);
    }

    public SlowDispatcher(Method toDispatch, Method defaultDispatch, List<Method> candidates) {
        this.toDispatch = toDispatch;
        this.defaultDispatch = Optional.ofNullable(defaultDispatch);
        this.dispatchTuples = new ArrayList<>();

        for (Method candidate : candidates) {
            candidate.setAccessible(true);
            dispatchTuples.add(new DispatchTuple(candidate, candidate.getParameterTypes()));
        }
    }

    @Override
    @RuntimeType
    public <T> Object invoke(@This T target, @AllArguments Object[] args) throws InvocationTargetException, IllegalAccessException {
        Method toCall = dispatchTuples.stream().filter(d -> d.isAssignable(args)).findFirst()
                .map(DispatchTuple::method)
                .or(() -> defaultDispatch)
                .orElseThrow(() -> new MultiException("No suitable candidate found to call for method " +
                        toDispatch.getName() + " and arguments of type " +
                        formatArgs(args)));

        return toCall.invoke(target, args);
    }

    private String formatArgs(Object[] args) {
        return Arrays.stream(args).map(Object::getClass).map(Class::getSimpleName).collect(Collectors.joining(","));
    }
}