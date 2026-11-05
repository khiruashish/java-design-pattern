package com.handbook.microservices.aggregation.declarative;

import com.handbook.microservices.aggregation.core.SimCall;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Aggregation as configuration: which backends to call and which fields to copy into the response. */
public final class AggregationSpec {

    /** "itinerary.flight <- flights.pnrStatus" style mapping. */
    public record FieldMapping(String responseField, String backend, String backendField) { }

    private final List<FieldMapping> mappings;

    public AggregationSpec(List<FieldMapping> mappings) {
        this.mappings = mappings;
    }

    /** Calls every backend in parallel once, then copies mapped fields; latency is the slowest call. */
    public Map<String, Object> execute(Map<String, SimCall> backends, StringBuilder timeline) {
        Map<String, Object> response = new LinkedHashMap<>();
        long slowest = 0;
        for (SimCall call : backends.values()) { // (1)!
            slowest = Math.max(slowest, call.latencyMs());
        }
        for (FieldMapping m : mappings) { // (2)!
            response.put(m.responseField(), backends.get(m.backend()).result().get(m.backendField()));
        }
        timeline.append(backends.size()).append(" backends in parallel, total ").append(slowest).append(" ms");
        return response;
    }
}
