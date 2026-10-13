package com.handbook.microservices.support;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A tiny, synchronous, in-memory stand-in for a message broker (Kafka, RabbitMQ).
 * Real brokers are asynchronous and at-least-once; the examples call that out where it matters.
 */
public final class EventBus {

    private final Map<String, List<Consumer<Object>>> subscribers = new LinkedHashMap<>();
    private final boolean log;

    public EventBus(boolean log) {
        this.log = log;
    }

    @SuppressWarnings("unchecked")
    public <T> void subscribe(String topic, Consumer<T> handler) {
        subscribers.computeIfAbsent(topic, t -> new ArrayList<>()).add(e -> handler.accept((T) e));
    }

    public void publish(String topic, Object event) {
        if (log) {
            System.out.println("  [bus] " + topic + " <- " + event);
        }
        for (Consumer<Object> s : subscribers.getOrDefault(topic, List.of())) {
            s.accept(event);
        }
    }
}
