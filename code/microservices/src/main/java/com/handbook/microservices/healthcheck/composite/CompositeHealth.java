package com.handbook.microservices.healthcheck.composite;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Aggregate component health by criticality: a broken optional dependency degrades, a broken critical one is DOWN. */
public final class CompositeHealth {

    public enum Status { UP, DEGRADED, DOWN }

    public record Component(String name, boolean critical, boolean healthy) { }

    public record Report(Status status, Map<String, String> details) { }

    public static Report evaluate(List<Component> components) {
        Map<String, String> details = new LinkedHashMap<>();
        Status overall = Status.UP;
        for (Component c : components) {
            details.put(c.name(), c.healthy() ? "UP" : "DOWN" + (c.critical() ? "" : " (optional)"));
            if (!c.healthy() && c.critical()) {
                overall = Status.DOWN; // (1)!
            } else if (!c.healthy() && overall == Status.UP) {
                overall = Status.DEGRADED; // (2)!
            }
        }
        return new Report(overall, details);
    }
}
