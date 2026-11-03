package com.handbook.microservices.caching.writebehind;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/** Write-behind: absorb writes in the cache, flush aggregated deltas to the database in batches. */
public final class WriteBehindCounter {

    private final Map<String, Long> pendingDeltas = new HashMap<>(); // the "cache" side
    private final Map<String, Long> database = new TreeMap<>();
    private int dbWrites;

    public synchronized void increment(String hotelId) {
        pendingDeltas.merge(hotelId, 1L, Long::sum); // (1)!
    }

    public synchronized int flush() { // (2)!
        int rows = pendingDeltas.size();
        pendingDeltas.forEach((id, delta) -> database.merge(id, delta, Long::sum));
        dbWrites += rows;
        pendingDeltas.clear();
        return rows;
    }

    public synchronized long pending() {
        return pendingDeltas.values().stream().mapToLong(Long::longValue).sum();
    }

    public synchronized Map<String, Long> database() {
        return Map.copyOf(database);
    }

    public synchronized int dbWrites() {
        return dbWrites;
    }
}
