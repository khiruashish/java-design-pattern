package com.handbook.microservices.eventsourcing.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An append-only store of events per stream (one stream per aggregate). Appends carry the version
 * the writer last saw; if someone else appended first, the write is rejected (optimistic locking).
 */
public final class EventStore<E> {

    public static final class ConcurrencyException extends RuntimeException {
        ConcurrencyException(String stream, long expected, long actual) {
            super(stream + ": expected version " + expected + " but was " + actual);
        }
    }

    private final Map<String, List<E>> streams = new HashMap<>();
    private int reads;

    public synchronized void append(String stream, long expectedVersion, List<E> events) {
        List<E> existing = streams.computeIfAbsent(stream, k -> new ArrayList<>());
        if (existing.size() != expectedVersion) { // (1)!
            throw new ConcurrencyException(stream, expectedVersion, existing.size());
        }
        existing.addAll(events);
    }

    /** Events with version greater than {@code afterVersion} (versions start at 1). */
    public synchronized List<E> read(String stream, long afterVersion) {
        List<E> all = streams.getOrDefault(stream, List.of());
        reads += all.size() - (int) afterVersion;
        return List.copyOf(all.subList((int) afterVersion, all.size()));
    }

    public synchronized int eventsReadSoFar() {
        return reads;
    }
}
