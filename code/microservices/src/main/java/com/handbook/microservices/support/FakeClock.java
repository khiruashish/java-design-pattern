package com.handbook.microservices.support;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

/** A controllable clock so time-based patterns (timeouts, windows, TTLs) give deterministic output. */
public final class FakeClock extends Clock {

    private Instant now;

    public FakeClock(Instant start) {
        this.now = start;
    }

    public static FakeClock at(String isoInstant) {
        return new FakeClock(Instant.parse(isoInstant));
    }

    public void advance(Duration d) {
        now = now.plus(d);
    }

    public void advanceMillis(long ms) {
        advance(Duration.ofMillis(ms));
    }

    @Override
    public Instant instant() {
        return now;
    }

    @Override
    public ZoneId getZone() {
        return ZoneOffset.UTC;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return this;
    }
}
