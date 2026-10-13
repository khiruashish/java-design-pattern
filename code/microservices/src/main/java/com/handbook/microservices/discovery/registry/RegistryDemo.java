package com.handbook.microservices.discovery.registry;

import com.handbook.microservices.discovery.registry.ServiceRegistry.Instance;
import com.handbook.microservices.support.FakeClock;

import java.time.Duration;

public class RegistryDemo {

    public static void main(String[] args) {
        FakeClock clock = FakeClock.at("2026-10-13T10:00:00Z");
        ServiceRegistry registry = new ServiceRegistry(clock, Duration.ofSeconds(30));

        System.out.println("=== Grocery: order-service instances come and go ===");
        registry.register("order-service", "order-1", "10.0.1.11:8080", "zone-a");
        registry.register("order-service", "order-2", "10.0.2.12:8080", "zone-b");
        registry.register("order-service", "order-3", "10.0.1.13:8080", "zone-a");
        print("t=0s   3 instances registered", registry);

        clock.advance(Duration.ofSeconds(20));
        registry.heartbeat("order-1");
        registry.heartbeat("order-3"); // order-2 has crashed and stops sending heartbeats
        print("t=20s  order-2 missed its heartbeat", registry);

        clock.advance(Duration.ofSeconds(15));
        registry.heartbeat("order-1");
        registry.heartbeat("order-3");
        print("t=35s  order-2 lease (30s) expired", registry);
        System.out.println("       evicted: " + registry.evictExpired());

        registry.deregister("order-3"); // graceful shutdown during a deploy
        print("t=35s  order-3 deregistered on shutdown", registry);

        registry.register("order-service", "order-4", "10.0.2.14:8080", "zone-b");
        print("t=35s  autoscaler adds order-4", registry);
    }

    private static void print(String label, ServiceRegistry registry) {
        System.out.println(label + " -> " + registry.lookup("order-service").stream()
                .map(Instance::address).toList());
    }
}
