package org.hueho.sandbox.stupid.multi.dispatchers;

import com.google.common.collect.ImmutableList;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import org.hueho.sandbox.stupid.multi.Dispatcher;
import org.hueho.sandbox.stupid.multi.exceptions.MultiException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SlowDispatcher implements Dispatcher {
    private final Method toDispatch;
    private final Optional<Method> defaultDispatch;
    private final List<DispatchTuple> dispatchTuples;

    public SlowDispatcher(Method toDispatch, Method defaultDispatch, List<DispatchTuple> candidates) {
        this.toDispatch = toDispatch;
        this.defaultDispatch = Optional.ofNullable(defaultDispatch);
        this.dispatchTuples = ImmutableList.copyOf(candidates);
        this.dispatchTuples.forEach(dt -> dt.method().setAccessible(true));
    }

    @Override
    @RuntimeType
    public <T> Object invoke(@This T target, @AllArguments Object[] args) throws InvocationTargetException, IllegalAccessException {
        Optional<DispatchTuple> toCall = dispatchTuples.stream().filter(d -> d.isAssignable(args)).findFirst();
        if (toCall.isPresent()) {
            return toCall.get().invoke(target, args);
        }

        if (defaultDispatch.isPresent()) {
            return defaultDispatch.get().invoke(target, args);
        }

        throw new MultiException("No suitable candidate found to call for method " +
                toDispatch.getName() + " and arguments of type " +
                formatArgs(args));
    }

    private String formatArgs(Object[] args) {
        return Arrays.stream(args).map(this::className).collect(Collectors.joining(","));
    }

    private String className(Object obj) {
        if (obj == null) return "<null>";
        else return obj.getClass().getSimpleName();
    }
}