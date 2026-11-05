package com.handbook.microservices.backpressure.overflow;

import java.util.ArrayDeque;
import java.util.Deque;

/** A bounded buffer must decide what happens when it's full: that decision IS the back-pressure policy. */
public final class IntakeBuffer {

    public enum Policy { REJECT_NEW, DROP_OLDEST, CALLER_WAITS }

    private final Deque<String> buffer = new ArrayDeque<>();
    private final int capacity;
    private final Policy policy;

    public IntakeBuffer(int capacity, Policy policy) {
        this.capacity = capacity;
        this.policy = policy;
    }

    /** @return what the producer is told. */
    public String offer(String item) {
        if (buffer.size() < capacity) {
            buffer.add(item);
            return "queued";
        }
        return switch (policy) { // (1)!
            case REJECT_NEW -> "429 Too Many Requests, Retry-After: 2";
            case DROP_OLDEST -> {
                String dropped = buffer.poll();
                buffer.add(item);
                yield "queued (dropped " + dropped + ")";
            }
            case CALLER_WAITS -> "producer blocked until space frees up";
        };
    }

    public Deque<String> contents() {
        return new ArrayDeque<>(buffer);
    }
}
