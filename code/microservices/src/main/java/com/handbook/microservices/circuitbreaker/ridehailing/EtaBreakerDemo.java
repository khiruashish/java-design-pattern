package com.handbook.microservices.circuitbreaker.ridehailing;

import com.handbook.microservices.circuitbreaker.core.CircuitBreaker;
import com.handbook.microservices.circuitbreaker.core.CircuitBreaker.Config;
import com.handbook.microservices.circuitbreaker.ridehailing.EtaService.Trip;
import com.handbook.microservices.support.FakeClock;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

public class EtaBreakerDemo {

    public static void main(String[] args) {
        FakeClock clock = FakeClock.at("2026-10-13T08:00:00Z");
        AtomicBoolean engineHealthy = new AtomicBoolean(true);
        var breaker = new CircuitBreaker("routing", new Config(5, 3, 60, Duration.ofSeconds(10), 1), clock);
        var eta = new EtaService(breaker, trip -> {
            if (!engineHealthy.get()) {
                throw new IllegalStateException("routing engine 503");
            }
            return (int) Math.round(trip.distanceKm() * 2.4);
        });

        System.out.println("=== Ride-hailing: ETA with a degraded fallback ===");
        System.out.println("trip-1 (5 km):  " + eta.eta(new Trip("trip-1", 5)));
        engineHealthy.set(false);
        System.out.println("-- routing engine fails during a deploy --");
        for (int i = 2; i <= 5; i++) {
            System.out.println("trip-" + i + " (8 km):  " + eta.eta(new Trip("trip-" + i, 8)));
        }
        System.out.println("-- 10 s later: one trial call while the engine is still down --");
        clock.advance(Duration.ofSeconds(10));
        System.out.println("trip-6 (8 km):  " + eta.eta(new Trip("trip-6", 8)));
        System.out.println("-- another 10 s: engine is back --");
        clock.advance(Duration.ofSeconds(10));
        engineHealthy.set(true);
        System.out.println("trip-7 (8 km):  " + eta.eta(new Trip("trip-7", 8)));
        System.out.println("Breaker state: " + breaker.state());
    }
}
