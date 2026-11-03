package com.handbook.microservices.caching.singleflight;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/** Request coalescing: concurrent misses for the same key share ONE load instead of stampeding the DB. */
public final class SingleFlightLoader<V> {

    private final Map<String, V> cache = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<V>> inFlight = new ConcurrentHashMap<>();
    private final Function<String, V> loader;

    public SingleFlightLoader(Function<String, V> loader) {
        this.loader = loader;
    }

    public V get(String key) {
        V cached = cache.get(key);
        if (cached != null) {
            return cached;
        }
        CompletableFuture<V> mine = new CompletableFuture<>();
        CompletableFuture<V> existing = inFlight.putIfAbsent(key, mine); // (1)!
        if (existing != null) {
            return existing.join(); // (2)!
        }
        try {
            V value = cache.get(key); // re-check: a load may have finished between our first check and now
            if (value == null) {
                value = loader.apply(key);
            }
            cache.put(key, value);
            mine.complete(value);
            return value;
        } catch (RuntimeException e) {
            mine.completeExceptionally(e);
            throw e;
        } finally {
            inFlight.remove(key);
        }
    }
}
