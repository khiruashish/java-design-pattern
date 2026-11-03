package com.handbook.microservices.caching.catalog;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** A minimal Redis stand-in: get / set with TTL / delete. */
public final class TtlCache<V> {

    private record Entry<V>(V value, Instant expiresAt) { }

    private final Map<String, Entry<V>> data = new ConcurrentHashMap<>();
    private final Clock clock;

    public TtlCache(Clock clock) {
        this.clock = clock;
    }

    public Optional<V> get(String key) {
        Entry<V> e = data.get(key);
        if (e == null || !clock.instant().isBefore(e.expiresAt())) {
            data.remove(key);
            return Optional.empty();
        }
        return Optional.of(e.value());
    }

    public void set(String key, V value, Duration ttl) {
        data.put(key, new Entry<>(value, clock.instant().plus(ttl)));
    }

    public void delete(String key) {
        data.remove(key);
    }
}
