package com.handbook.enterprise.di.container;

import java.lang.reflect.Constructor;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** A 60-line IoC container: constructor autowiring by type, singletons, cycle detection. */
public final class MiniContainer {

    private final Map<Class<?>, Class<?>> bindings = new HashMap<>();
    private final Map<Class<?>, Object> singletons = new HashMap<>();
    private final Deque<Class<?>> resolving = new ArrayDeque<>();

    public <T> MiniContainer bind(Class<T> type, Class<? extends T> impl) { // (1)!
        bindings.put(type, impl);
        return this;
    }

    public <T> T get(Class<T> type) {
        Class<?> impl = bindings.getOrDefault(type, type);
        Object existing = singletons.get(impl);
        if (existing != null) {
            return type.cast(existing);
        }
        if (resolving.contains(impl)) { // (2)!
            List<Class<?>> path = new java.util.ArrayList<>(resolving);
            java.util.Collections.reverse(path);
            path.add(impl);
            throw new IllegalStateException("Circular dependency: "
                    + path.stream().map(Class::getSimpleName).collect(Collectors.joining(" -> ")));
        }
        if (impl.isInterface()) {
            throw new IllegalStateException("No binding for " + type.getSimpleName());
        }
        resolving.push(impl);
        try {
            Constructor<?> ctor = impl.getConstructors()[0]; // (3)!
            Object[] args = new Object[ctor.getParameterCount()];
            for (int i = 0; i < args.length; i++) {
                args[i] = get(ctor.getParameterTypes()[i]);
            }
            Object instance = ctor.newInstance(args);
            singletons.put(impl, instance);
            return type.cast(instance);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot create " + impl.getSimpleName(), e);
        } finally {
            resolving.pop();
        }
    }
}
