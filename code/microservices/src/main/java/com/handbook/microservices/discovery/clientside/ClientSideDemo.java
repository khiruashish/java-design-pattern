package com.handbook.microservices.discovery.clientside;

import com.handbook.microservices.discovery.registry.ServiceRegistry;
import com.handbook.microservices.support.FakeClock;

import java.time.Duration;
import java.util.Set;
import java.util.function.BiFunction;

public class ClientSideDemo {

    public static void main(String[] args) {
        FakeClock clock = FakeClock.at("2026-10-13T10:00:00Z");
        ServiceRegistry registry = new ServiceRegistry(clock, Duration.ofSeconds(30));
        registry.register("eta-service", "eta-1", "10.0.1.21:9000", "zone-a");
        registry.register("eta-service", "eta-2", "10.0.1.22:9000", "zone-a");
        registry.register("eta-service", "eta-3", "10.0.2.23:9000", "zone-b");

        Set<String> crashed = Set.of("10.0.1.22:9000"); // crashed 5s ago; its lease hasn't expired yet
        BiFunction<String, String, String> network = (address, request) -> {
            if (crashed.contains(address)) {
                throw new IllegalStateException("connection refused");
            }
            return address + " answered " + request;
        };

        System.out.println("=== Ride-hailing: trip service calls eta-service via client-side discovery ===");
        var client = new LoadBalancedClient(registry);
        for (int i = 1; i <= 5; i++) {
            System.out.println("ETA request " + i + ": " + client.call("eta-service", "eta(trip-" + i + ")", network));
        }
    }
}
