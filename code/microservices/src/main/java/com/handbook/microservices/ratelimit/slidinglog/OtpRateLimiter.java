package com.handbook.microservices.ratelimit.slidinglog;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Sliding-window log: remember the timestamp of each allowed request and count those inside the
 * last window. Exact (no boundary bursts), at the cost of storing one entry per request.
 * Used here to stop OTP SMS abuse (each SMS costs money and can be used to spam a victim).
 */
public final class OtpRateLimiter {

    private final int maxRequests;
    private final Duration window;
    private final Clock clock;
    private final Map<String, Deque<Instant>> logs = new ConcurrentHashMap<>();

    public OtpRateLimiter(int maxRequests, Duration window, Clock clock) {
        this.maxRequests = maxRequests;
        this.window = window;
        this.clock = clock;
    }

    /** Returns 0 if allowed, otherwise the seconds to wait. */
    public long tryRequest(String phone) {
        Instant now = clock.instant();
        Deque<Instant> log = logs.computeIfAbsent(phone, p -> new ArrayDeque<>());
        synchronized (log) {
            while (!log.isEmpty() && !log.peekFirst().isAfter(now.minus(window))) { // (1)!
                log.pollFirst();
            }
            if (log.size() < maxRequests) {
                log.addLast(now);
                return 0;
            }
            return Duration.between(now, log.peekFirst().plus(window)).toSeconds(); // (2)!
        }
    }
}
