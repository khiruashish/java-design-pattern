package com.handbook.microservices.ratelimit.distributed;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Stands in for Redis. {@code incrementAndGet} mirrors an atomic {@code INCR key} followed by
 * {@code EXPIRE key ttl} on first use; old windows simply expire.
 */
public final class SharedCounterStore {

    private final Map<String, AtomicLong> counters = new ConcurrentHashMap<>();

    public long incrementAndGet(String key) {
        return counters.computeIfAbsent(key, k -> new AtomicLong()).incrementAndGet();
    }
}
