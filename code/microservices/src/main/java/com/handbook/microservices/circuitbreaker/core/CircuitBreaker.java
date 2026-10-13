package com.handbook.microservices.circuitbreaker.core;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A count-based circuit breaker modelled on Resilience4j's state machine.
 * <pre>
 *   CLOSED --(failure rate >= threshold over the last N calls)--> OPEN
 *   OPEN   --(wait duration elapsed)--> HALF_OPEN
 *   HALF_OPEN --(trial calls succeed)--> CLOSED, --(any trial call fails)--> OPEN
 * </pre>
 */
public final class CircuitBreaker {

    public enum State { CLOSED, OPEN, HALF_OPEN }

    public record Config(int windowSize, int minimumCalls, int failureRatePercent,
                         Duration waitInOpen, int trialCalls) {
    }

    public static final class OpenException extends RuntimeException {
        OpenException(String name) {
            super(name + " circuit is OPEN");
        }
    }

    private final String name;
    private final Config config;
    private final Clock clock;
    private final Deque<Boolean> window = new ArrayDeque<>(); // true = failure
    private State state = State.CLOSED;
    private Instant openedAt;
    private int trialSuccesses;

    public CircuitBreaker(String name, Config config, Clock clock) {
        this.name = name;
        this.config = config;
        this.clock = clock;
    }

    public synchronized State state() {
        if (state == State.OPEN && !clock.instant().isBefore(openedAt.plus(config.waitInOpen()))) {
            transition(State.HALF_OPEN); // (1)!
        }
        return state;
    }

    public <T> T call(Supplier<T> action, Function<Throwable, T> fallback) {
        try {
            return call(action);
        } catch (RuntimeException e) {
            return fallback.apply(e); // (2)!
        }
    }

    public <T> T call(Supplier<T> action) {
        if (state() == State.OPEN) {
            throw new OpenException(name); // (3)!
        }
        try {
            T result = action.get();
            record(false);
            return result;
        } catch (RuntimeException e) {
            record(true);
            throw e;
        }
    }

    private synchronized void record(boolean failure) {
        if (state == State.HALF_OPEN) { // (4)!
            if (failure) {
                trip();
            } else if (++trialSuccesses >= config.trialCalls()) {
                window.clear();
                transition(State.CLOSED);
            }
            return;
        }
        window.addLast(failure);
        if (window.size() > config.windowSize()) {
            window.removeFirst();
        }
        long failures = window.stream().filter(f -> f).count();
        if (window.size() >= config.minimumCalls() // (5)!
                && failures * 100 / window.size() >= config.failureRatePercent()) {
            trip();
        }
    }

    private void trip() {
        openedAt = clock.instant();
        transition(State.OPEN);
    }

    private void transition(State to) {
        System.out.println("    [" + name + "] " + state + " -> " + to);
        state = to;
        trialSuccesses = 0;
    }
}
