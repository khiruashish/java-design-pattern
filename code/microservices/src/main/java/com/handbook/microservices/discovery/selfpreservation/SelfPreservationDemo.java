package com.handbook.microservices.discovery.selfpreservation;

import com.handbook.microservices.support.FakeClock;

import java.time.Duration;

public class SelfPreservationDemo {

    public static void main(String[] args) {
        FakeClock clock = FakeClock.at("2026-10-13T10:00:00Z");
        var registry = new SelfPreservingRegistry(clock, Duration.ofSeconds(30), 0.5);
        for (int i = 1; i <= 4; i++) {
            registry.register("cart-service", "cart-" + i, "10.0.0." + i, "zone-a");
        }

        System.out.println("=== Challenge: self-preservation during a network partition ===");
        clock.advance(Duration.ofSeconds(40));
        registry.heartbeat("cart-1");
        registry.heartbeat("cart-2");
        registry.heartbeat("cart-3"); // only cart-4 really crashed
        System.out.println("1 of 4 expired  -> evicted " + registry.evictExpired());

        clock.advance(Duration.ofSeconds(40)); // partition: no heartbeats reach the registry
        System.out.println("3 of 3 expired  -> evicted " + registry.evictExpired());
        System.out.println("Callers still get " + registry.lookup("cart-service").size()
                + " instances (possibly stale, but better than none; clients retry on failure)");

        registry.heartbeat("cart-1");
        registry.heartbeat("cart-2");
        registry.heartbeat("cart-3");
        System.out.println("Partition heals -> evicted " + registry.evictExpired()
                + ", lookup returns " + registry.lookup("cart-service").size() + " instances");
    }
}
