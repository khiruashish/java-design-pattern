package com.handbook.microservices.circuitbreaker.ridehailing;

import com.handbook.microservices.circuitbreaker.core.CircuitBreaker;

import java.util.function.ToIntFunction;

/**
 * The traffic-aware routing engine gives accurate ETAs. If it's unhealthy, the breaker opens and
 * riders get a rougher estimate from straight-line distance instead of an error screen.
 */
public final class EtaService {

    public record Trip(String id, double distanceKm) {
    }

    private final CircuitBreaker breaker;
    private final ToIntFunction<Trip> routingEngine;

    public EtaService(CircuitBreaker breaker, ToIntFunction<Trip> routingEngine) {
        this.breaker = breaker;
        this.routingEngine = routingEngine;
    }

    public String eta(Trip trip) {
        return breaker.call(
                () -> routingEngine.applyAsInt(trip) + " min (live traffic)",
                e -> estimate(trip) + " min (estimate)"); // (1)!
    }

    private static int estimate(Trip trip) {
        return (int) Math.ceil(trip.distanceKm() / 25.0 * 60 * 1.3); // 25 km/h city average, 30% detour
    }
}
