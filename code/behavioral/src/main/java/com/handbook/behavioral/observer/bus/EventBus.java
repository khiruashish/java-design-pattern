package com.handbook.behavioral.observer.bus;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/** Challenge solution: typed, thread-safe event bus with closeable subscriptions. */
public class EventBus {

    private final Map<Class<?>, List<Consumer<Object>>> handlers = new ConcurrentHashMap<>();

    public <T> AutoCloseable subscribe(Class<T> type, Consumer<? super T> handler) {
        Consumer<Object> wrapper = event -> handler.accept(type.cast(event));
        handlers.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).add(wrapper);
        return () -> handlers.get(type).remove(wrapper);
    }

    public void publish(Object event) {
        handlers.getOrDefault(event.getClass(), List.of()).forEach(h -> h.accept(event));
    }
}
