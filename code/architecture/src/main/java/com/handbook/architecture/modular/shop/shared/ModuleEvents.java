package com.handbook.architecture.modular.shop.shared;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/** In-process event bus shared by modules (Spring's ApplicationEventPublisher in a real app). */
public final class ModuleEvents {

    private final Map<Class<?>, List<Consumer<Object>>> listeners = new LinkedHashMap<>();

    @SuppressWarnings("unchecked")
    public <E> void on(Class<E> type, Consumer<E> listener) {
        listeners.computeIfAbsent(type, t -> new ArrayList<>()).add(e -> listener.accept((E) e));
    }

    public void publish(Object event) {
        listeners.getOrDefault(event.getClass(), List.of()).forEach(l -> l.accept(event));
    }
}
