package org.hueho.sandbox.stupid.multi.dispatchers;

import java.lang.reflect.Method;

public record DispatchTuple(Method method, Class<?> ...parameterClass) {
    public boolean isAssignable(Object[] args) {
        assert args.length == parameterClass.length;

        for (int i = 0; i < args.length; i++) {
            if(!parameterClass[i].isInstance(args[i])) return false;
        }

        return true;
    }
}