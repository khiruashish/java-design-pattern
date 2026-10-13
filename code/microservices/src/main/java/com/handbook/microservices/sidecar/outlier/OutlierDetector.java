package com.handbook.microservices.sidecar.outlier;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Coding challenge: Envoy-style outlier detection. After N consecutive 5xx from one host, eject it
 * from the load-balancing pool for a while, but never eject more than a maximum share of hosts.
 */
public final class OutlierDetector {

    private final List<String> hosts;
    private final int consecutive5xx;
    private final Duration ejectionTime;
    private final int maxEjectionPercent;
    private final Clock clock;
    private final Map<String, Integer> failures = new HashMap<>();
    private final Map<String, Instant> ejectedUntil = new HashMap<>();

    public OutlierDetector(List<String> hosts, int consecutive5xx, Duration ejectionTime,
                           int maxEjectionPercent, Clock clock) {
        this.hosts = List.copyOf(hosts);
        this.consecutive5xx = consecutive5xx;
        this.ejectionTime = ejectionTime;
        this.maxEjectionPercent = maxEjectionPercent;
        this.clock = clock;
    }

    public List<String> healthyHosts() {
        Instant now = clock.instant();
        ejectedUntil.values().removeIf(until -> !now.isBefore(until));
        return hosts.stream().filter(h -> !ejectedUntil.containsKey(h)).toList();
    }

    public void record(String host, int status) {
        if (status < 500) {
            failures.remove(host);
            return;
        }
        int n = failures.merge(host, 1, Integer::sum);
        boolean underCap = (ejectedUntil.size() + 1) * 100 / hosts.size() <= maxEjectionPercent;
        if (n >= consecutive5xx && underCap && !ejectedUntil.containsKey(host)) {
            ejectedUntil.put(host, clock.instant().plus(ejectionTime));
            failures.remove(host);
            System.out.println("    ejected " + host + " for " + ejectionTime.toSeconds() + "s");
        }
    }
}
