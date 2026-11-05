package com.handbook.concurrency.dcl.tenants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** Lazy, thread-safe creation PER KEY: the modern replacement for hand-written DCL. */
public class TenantResources {

    public record SearchIndex(String tenant) {
        static final AtomicInteger CREATED = new AtomicInteger();

        public SearchIndex {
            CREATED.incrementAndGet(); // expensive: opens index files, warms caches
        }
    }

    private final Map<String, SearchIndex> indexes = new ConcurrentHashMap<>();

    public SearchIndex indexFor(String tenant) {
        return indexes.computeIfAbsent(tenant, SearchIndex::new); // (1)!
    }

    public int created() {
        return SearchIndex.CREATED.get();
    }
}
