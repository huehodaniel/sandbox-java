package org.hueho.sandbox.stupid.multi.dispatchers;

import org.hueho.sandbox.stupid.util.ArrayUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public record DispatchTuple(Method method, ParameterOrder order, Class<?>... parameterClass) {
    public boolean isAssignable(Object[] args) {
        assert args.length == parameterClass.length;

        try {
            prepareArgs(args);

            for (int i = 0; i < args.length; i++) {
                if (!parameterClass[i].isInstance(args[i])) return false;
            }

            return true;
        } finally {
            prepareArgs(args);
        }
    }

    public Object invoke(Object target, Object[] args) throws InvocationTargetException, IllegalAccessException {
        assert isAssignable(args);

        try {
            prepareArgs(args);

            return method.invoke(target, args);
        } finally {
            prepareArgs(args);
        }
    }

    private void prepareArgs(Object[] args) {
        if (order == ParameterOrder.MIRRORED) ArrayUtils.reverse(args);
    }

    public enum ParameterOrder {
        DEFAULT,
        MIRRORED;
    }
}