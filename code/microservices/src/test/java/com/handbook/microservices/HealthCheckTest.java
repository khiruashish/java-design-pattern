package com.handbook.microservices;

import com.handbook.microservices.healthcheck.cached.CachedHealthCheck;
import com.handbook.microservices.healthcheck.composite.CompositeHealth;
import com.handbook.microservices.healthcheck.composite.CompositeHealth.Component;
import com.handbook.microservices.healthcheck.composite.CompositeHealth.Status;
import com.handbook.microservices.healthcheck.probes.GroceryProbes;
import com.handbook.microservices.support.FakeClock;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HealthCheckTest {

    @Test
    void databaseOutageAffectsReadinessOnly() {
        GroceryProbes p = new GroceryProbes(() -> true, () -> false, () -> true);
        assertThat(p.liveness()).isEqualTo(200);
        assertThat(p.readiness()).isEqualTo(503);
    }

    @Test
    void optionalFailureDegradesCriticalFailureIsDown() {
        assertThat(CompositeHealth.evaluate(List.of(new Component("a", true, true), new Component("b", false, false))).status())
                .isEqualTo(Status.DEGRADED);
        assertThat(CompositeHealth.evaluate(List.of(new Component("a", true, false), new Component("b", false, true))).status())
                .isEqualTo(Status.DOWN);
    }

    @Test
    void expensiveCheckRunsOncePerInterval() {
        FakeClock clock = FakeClock.at("2026-01-01T00:00:00Z");
        CachedHealthCheck c = new CachedHealthCheck(() -> true, clock, Duration.ofSeconds(10));
        c.healthy();
        clock.advance(Duration.ofSeconds(9));
        c.healthy();
        clock.advance(Duration.ofSeconds(1));
        c.healthy();
        assertThat(c.executions()).isEqualTo(2);
    }
}
