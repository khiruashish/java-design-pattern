package com.handbook.structural.decorator.caching;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** Challenge solution: transparent read-through cache in front of any catalog. */
public class CachingCatalog implements ProductCatalog {

    private final ProductCatalog inner;
    private final Map<String, String> cache = new ConcurrentHashMap<>();
    private final AtomicInteger hits = new AtomicInteger();
    private final AtomicInteger misses = new AtomicInteger();

    public CachingCatalog(ProductCatalog inner) {
        this.inner = inner;
    }

    @Override
    public String describe(String sku) {
        String cached = cache.get(sku);
        if (cached != null) {
            hits.incrementAndGet();
            return cached;
        }
        misses.incrementAndGet();
        return cache.computeIfAbsent(sku, inner::describe);
    }

    public void evict(String sku) {
        cache.remove(sku);
    }

    public String stats() {
        return "hits=" + hits + " misses=" + misses;
    }
}
