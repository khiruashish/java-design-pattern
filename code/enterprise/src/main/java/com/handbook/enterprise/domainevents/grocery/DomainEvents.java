package com.handbook.enterprise.domainevents.grocery;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/** Aggregates RECORD events; the application layer DISPATCHES them after a successful commit. */
public final class DomainEvents {

    public sealed interface Event permits OrderPlaced, OrderCancelled { } // (1)!

    public record OrderPlaced(String orderId, String customerId, long totalPaise) implements Event { }

    public record OrderCancelled(String orderId, String reason) implements Event { }

    /** Base class for roots: collects events in memory while the transaction runs. */
    public abstract static class AggregateRoot {
        private final List<Event> pending = new ArrayList<>();

        protected void record(Event e) { pending.add(e); } // (2)!

        public List<Event> pullEvents() {
            List<Event> out = List.copyOf(pending);
            pending.clear();
            return out;
        }
    }

    /** In-process publisher: handlers subscribe by event type. */
    public static final class Dispatcher {
        private final Map<Class<?>, List<Consumer<Event>>> handlers = new LinkedHashMap<>();

        public <E extends Event> Dispatcher on(Class<E> type, Consumer<E> handler) {
            handlers.computeIfAbsent(type, k -> new ArrayList<>()).add(e -> handler.accept(type.cast(e)));
            return this;
        }

        public void dispatch(List<Event> events) {
            for (Event e : events) {
                handlers.getOrDefault(e.getClass(), List.of()).forEach(h -> h.accept(e));
            }
        }
    }

    private DomainEvents() {
    }
}
