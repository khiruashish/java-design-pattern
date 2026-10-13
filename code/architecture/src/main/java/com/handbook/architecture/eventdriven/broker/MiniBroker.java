package com.handbook.architecture.eventdriven.broker;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A tiny in-process broker with Kafka-like consumer groups: every GROUP receives every event
 * (publish-subscribe), and within a group the events are spread across its members (competing
 * consumers). Synchronous for readability; real brokers are asynchronous and durable.
 */
public final class MiniBroker {

    private record Group(List<Consumer<Object>> members, int[] next) {
    }

    private final Map<String, Map<String, Group>> topics = new LinkedHashMap<>();

    @SuppressWarnings("unchecked")
    public <E> void subscribe(String topic, String group, Consumer<E> member) {
        topics.computeIfAbsent(topic, t -> new LinkedHashMap<>())
                .computeIfAbsent(group, g -> new Group(new ArrayList<>(), new int[1]))
                .members().add(e -> member.accept((E) e));
    }

    public void publish(String topic, Object event) {
        System.out.println("[" + topic + "] " + event);
        topics.getOrDefault(topic, Map.of()).forEach((groupName, g) -> {
            int i = g.next()[0]++ % g.members().size(); // (1)!
            g.members().get(i).accept(event);
        });
    }
}
