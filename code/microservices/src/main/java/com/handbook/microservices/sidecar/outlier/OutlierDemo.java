package com.handbook.microservices.sidecar.outlier;

import com.handbook.microservices.support.FakeClock;

import java.time.Duration;
import java.util.List;

public class OutlierDemo {

    public static void main(String[] args) {
        FakeClock clock = FakeClock.at("2026-10-13T12:00:00Z");
        var detector = new OutlierDetector(List.of("pod-a", "pod-b", "pod-c", "pod-d"), 3,
                Duration.ofSeconds(30), 50, clock);

        System.out.println("=== Challenge: outlier detection (3 consecutive 5xx, max 50% ejected) ===");
        System.out.println("healthy: " + detector.healthyHosts());
        for (int i = 0; i < 3; i++) {
            detector.record("pod-b", 503); // pod-b has a corrupted cache
        }
        System.out.println("healthy: " + detector.healthyHosts());
        for (String pod : List.of("pod-c", "pod-d")) {
            for (int i = 0; i < 3; i++) {
                detector.record(pod, 503); // a bad dependency makes more pods fail
            }
        }
        System.out.println("healthy: " + detector.healthyHosts() + "  (pod-d kept: ejecting it would exceed 50%)");
        clock.advance(Duration.ofSeconds(30));
        System.out.println("30 s later healthy: " + detector.healthyHosts());
    }
}
