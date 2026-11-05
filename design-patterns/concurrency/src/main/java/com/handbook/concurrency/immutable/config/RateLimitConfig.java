package com.handbook.concurrency.immutable.config;

import java.util.concurrent.atomic.AtomicReference;

/** Lock-free configuration: readers get an immutable snapshot; updates swap the whole snapshot. */
public final class RateLimitConfig {

    public record Snapshot(int version, int requestsPerMinute, int burst) { // (1)!
        public Snapshot {
            if (burst > requestsPerMinute) {
                throw new IllegalArgumentException("burst cannot exceed the per-minute limit");
            }
        }

        Snapshot withLimits(int rpm, int burst) {
            return new Snapshot(version + 1, rpm, burst);
        }
    }

    private final AtomicReference<Snapshot> current = new AtomicReference<>(new Snapshot(1, 600, 60));

    public Snapshot get() {
        return current.get(); // (2)!
    }

    public void update(int rpm, int burst) {
        current.updateAndGet(s -> s.withLimits(rpm, burst)); // (3)!
    }
}
