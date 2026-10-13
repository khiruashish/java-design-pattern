package com.handbook.microservices.ratelimit.slidingcounter;

import java.time.Clock;

/**
 * Coding challenge: the sliding-window counter. Keeps just two numbers (current and previous fixed
 * window) and weights the previous window by how much of it still overlaps the sliding window.
 * Nearly as accurate as a log, with O(1) memory per key (Cloudflare's approach).
 */
public final class SlidingWindowCounter {

    private final long limit;
    private final long windowMillis;
    private final Clock clock;
    private long currentWindow = -1;
    private long currentCount;
    private long previousCount;

    public SlidingWindowCounter(long limit, long windowMillis, Clock clock) {
        this.limit = limit;
        this.windowMillis = windowMillis;
        this.clock = clock;
    }

    public synchronized boolean tryAcquire() {
        long now = clock.millis();
        long window = now / windowMillis;
        if (window != currentWindow) {
            previousCount = window == currentWindow + 1 ? currentCount : 0;
            currentCount = 0;
            currentWindow = window;
        }
        double elapsedFraction = (double) (now % windowMillis) / windowMillis;
        double estimate = previousCount * (1 - elapsedFraction) + currentCount;
        if (estimate < limit) {
            currentCount++;
            return true;
        }
        return false;
    }
}
