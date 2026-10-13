package com.handbook.microservices;

import com.handbook.microservices.sidecar.mtls.SidecarProxy;
import com.handbook.microservices.sidecar.outlier.OutlierDetector;
import com.handbook.microservices.support.FakeClock;
import com.handbook.microservices.support.Http.Request;
import com.handbook.microservices.support.Http.Response;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SidecarTest {

    @Test
    void onlyAllowedIdentitiesReachTheApp() {
        var sidecar = new SidecarProxy("me", Map.of("/x", Set.of("good")), r -> Response.of(200, "ok"));
        assertThat(sidecar.inbound(Request.get("/x", Map.of("x-peer-spiffe-id", "good"))).status()).isEqualTo(200);
        assertThat(sidecar.inbound(Request.get("/x", Map.of("x-peer-spiffe-id", "evil"))).status()).isEqualTo(403);
        assertThat(sidecar.inbound(Request.get("/x", Map.of())).status()).isEqualTo(403);
    }

    @Test
    void outlierIsEjectedThenReturns() {
        FakeClock clock = FakeClock.at("2026-01-01T00:00:00Z");
        var detector = new OutlierDetector(List.of("a", "b"), 2, Duration.ofSeconds(10), 50, clock);
        detector.record("a", 500);
        detector.record("a", 500);
        assertThat(detector.healthyHosts()).containsExactly("b");
        clock.advance(Duration.ofSeconds(10));
        assertThat(detector.healthyHosts()).containsExactly("a", "b");
    }
}
