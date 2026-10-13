package com.handbook.capstone.shared;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/** [Observer] In-process event bus between modules. Kafka would replace it if modules became services. */
public final class DomainEvents {

    /** Every event carries a unique ID so consumers can deduplicate redeliveries. */
    public interface Event {
        String eventId();
    }

    public record OrderPlaced(String eventId, String orderId, String customerId, Money total) implements Event {
    }

    public record OrderStatusChanged(String eventId, String orderId, String customerId, String status) implements Event {
    }

    public record StockLow(String eventId, String sku, int left) implements Event {
    }

    private final Map<Class<?>, List<Consumer<Object>>> listeners = new LinkedHashMap<>();

    @SuppressWarnings("unchecked")
    public <E extends Event> void subscribe(Class<E> type, Consumer<E> listener) {
        listeners.computeIfAbsent(type, t -> new ArrayList<>()).add(e -> listener.accept((E) e));
    }

    public void publish(Event event) {
        listeners.getOrDefault(event.getClass(), List.of()).forEach(l -> l.accept(event));
    }
}
