package com.handbook.microservices;

import com.handbook.microservices.discovery.registry.ServiceRegistry;
import com.handbook.microservices.discovery.zone.ZoneAwareBalancer;
import com.handbook.microservices.support.FakeClock;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceDiscoveryTest {

    @Test
    void instanceDisappearsWhenLeaseExpires() {
        FakeClock clock = FakeClock.at("2026-01-01T00:00:00Z");
        var registry = new ServiceRegistry(clock, Duration.ofSeconds(30));
        registry.register("s", "a", "10.0.0.1", "z1");
        registry.register("s", "b", "10.0.0.2", "z1");
        clock.advance(Duration.ofSeconds(25));
        registry.heartbeat("a");
        clock.advance(Duration.ofSeconds(10));
        assertThat(registry.lookup("s")).extracting(ServiceRegistry.Instance::id).containsExactly("a");
    }

    @Test
    void zoneAwareFallsBackWhenLocalZoneTooSmall() {
        FakeClock clock = FakeClock.at("2026-01-01T00:00:00Z");
        var registry = new ServiceRegistry(clock, Duration.ofSeconds(30));
        registry.register("s", "a1", "x", "a");
        registry.register("s", "b1", "y", "b");
        var balancer = new ZoneAwareBalancer(registry, "a", 1);
        assertThat(balancer.choose("s").id()).isEqualTo("a1");
        assertThat(new ZoneAwareBalancer(registry, "a", 2).choose("s").id()).isIn("a1", "b1");
    }
}
