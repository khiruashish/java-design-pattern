package com.handbook.capstone.shared;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** [Singleton] One ID generator per JVM (enum singleton: thread-safe, serialization-safe). */
public enum Ids {
    INSTANCE;

    private final Map<String, AtomicInteger> counters = new ConcurrentHashMap<>();

    public String next(String prefix) {
        return prefix + "-" + counters.computeIfAbsent(prefix, p -> new AtomicInteger(100)).incrementAndGet();
    }
}
