package com.handbook.microservices.idempotency.notifications;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Remembers recently sent notification keys for a limited time, like Redis
 * {@code SET key 1 NX EX 86400}. Memory stays bounded because old keys expire; duplicates arriving
 * later than the window are rare and, for notifications, acceptable.
 */
public final class SmsDeduplicator {

    private final Map<String, Instant> seen = new LinkedHashMap<>();
    private final Duration window;
    private final Clock clock;

    public SmsDeduplicator(Duration window, Clock clock) {
        this.window = window;
        this.clock = clock;
    }

    /** Atomically "set if absent": true means this key is new and the SMS should be sent. */
    public synchronized boolean firstTime(String dedupeKey) {
        Instant now = clock.instant();
        seen.values().removeIf(expiry -> !now.isBefore(expiry)); // (1)!
        return seen.putIfAbsent(dedupeKey, now.plus(window)) == null; // (2)!
    }

    public synchronized int size() {
        return seen.size();
    }
}
