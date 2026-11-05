package com.handbook.microservices.aggregation.core;

import java.util.Map;

/** A backend call with a known latency, so aggregation timelines are deterministic (virtual milliseconds). */
public record SimCall(String backend, long latencyMs, Map<String, Object> result, boolean fails) {

    public static SimCall ok(String backend, long latencyMs, Map<String, Object> result) {
        return new SimCall(backend, latencyMs, result, false);
    }

    public static SimCall failing(String backend, long latencyMs) {
        return new SimCall(backend, latencyMs, Map.of(), true);
    }
}
