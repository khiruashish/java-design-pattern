package com.handbook.microservices.discovery.zone;

import com.handbook.microservices.discovery.registry.ServiceRegistry;
import com.handbook.microservices.discovery.registry.ServiceRegistry.Instance;
import com.handbook.microservices.support.FakeClock;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ZoneAwareDemo {

    public static void main(String[] args) {
        FakeClock clock = FakeClock.at("2026-10-13T10:00:00Z");
        ServiceRegistry registry = new ServiceRegistry(clock, Duration.ofSeconds(30));
        registry.register("ledger-service", "ledger-a1", "10.1.0.11", "zone-a");
        registry.register("ledger-service", "ledger-a2", "10.1.0.12", "zone-a");
        registry.register("ledger-service", "ledger-b1", "10.2.0.11", "zone-b");
        registry.register("ledger-service", "ledger-c1", "10.3.0.11", "zone-c");

        var balancer = new ZoneAwareBalancer(registry, "zone-a", 2);

        System.out.println("=== Payments: zone-aware routing from a caller in zone-a ===");
        System.out.println("Both zone-a instances healthy -> " + picks(balancer, 4));

        registry.deregister("ledger-a2"); // zone-a loses an instance
        System.out.println("Only 1 left in zone-a (min 2) -> " + picks(balancer, 4));
    }

    private static List<String> picks(ZoneAwareBalancer balancer, int n) {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Instance chosen = balancer.choose("ledger-service");
            ids.add(chosen.id());
        }
        return ids;
    }
}
