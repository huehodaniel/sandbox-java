package org.hueho.sandbox.stupid.multi;

import java.lang.reflect.InvocationTargetException;

public interface Dispatcher {
    static String INVOKE = "invoke";

    <T> Object invoke(T target, Object[] args) throws InvocationTargetException, IllegalAccessException;
}
