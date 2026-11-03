package com.handbook.microservices.healthcheck.cached;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.function.BooleanSupplier;

/** Run the expensive dependency check at most once per interval; probes read the cached result. */
public final class CachedHealthCheck {

    private final BooleanSupplier expensiveCheck;
    private final Clock clock;
    private final Duration interval;
    private Instant checkedAt = Instant.MIN;
    private boolean lastResult;
    private int executions;

    public CachedHealthCheck(BooleanSupplier expensiveCheck, Clock clock, Duration interval) {
        this.expensiveCheck = expensiveCheck;
        this.clock = clock;
        this.interval = interval;
    }

    public synchronized boolean healthy() {
        Instant now = clock.instant();
        if (checkedAt == Instant.MIN || !now.isBefore(checkedAt.plus(interval))) { // (1)!
            lastResult = expensiveCheck.getAsBoolean();
            checkedAt = now;
            executions++;
        }
        return lastResult;
    }

    public int executions() {
        return executions;
    }
}
