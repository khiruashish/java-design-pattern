package com.handbook.microservices.resilience.retry;

import java.util.Random;
import java.util.function.LongConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Retry with exponential backoff and "full jitter": wait a random time between 0 and
 * min(cap, base * 2^attempt). Only transient errors are retried.
 */
public final class Retry {

    private final int maxAttempts;
    private final long baseMillis;
    private final long capMillis;
    private final Predicate<RuntimeException> retryable;
    private final Random random;
    private final LongConsumer sleeper;

    public Retry(int maxAttempts, long baseMillis, long capMillis, Predicate<RuntimeException> retryable,
                 Random random, LongConsumer sleeper) {
        this.maxAttempts = maxAttempts;
        this.baseMillis = baseMillis;
        this.capMillis = capMillis;
        this.retryable = retryable;
        this.random = random;
        this.sleeper = sleeper; // (1)!
    }

    public <T> T call(Supplier<T> action) {
        for (int attempt = 1; ; attempt++) {
            try {
                return action.get();
            } catch (RuntimeException e) {
                if (!retryable.test(e) || attempt == maxAttempts) { // (2)!
                    throw e;
                }
                long ceiling = Math.min(capMillis, baseMillis * (1L << attempt));
                long wait = (long) (random.nextDouble() * ceiling); // (3)!
                System.out.printf("    attempt %d failed (%s), retrying in %d ms (max %d)%n",
                        attempt, e.getMessage(), wait, ceiling);
                sleeper.accept(wait);
            }
        }
    }
}
