package org.hueho.sandbox.stupid.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClassToClassMap {
    private final Map<Class<?>, Class<?>> map = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T> Class<? extends T> get(Class<T> inClass) {
        return (Class<? extends T>) map.get(inClass);
    }

    public <T> void put(Class<T> inClass, Class<? extends T> outClass) {
        map.put(inClass, outClass);
    }

    public void putUnsafe(Class<?> inClass, Class<?> outClass) {
        map.put(inClass, outClass);
    }
}
