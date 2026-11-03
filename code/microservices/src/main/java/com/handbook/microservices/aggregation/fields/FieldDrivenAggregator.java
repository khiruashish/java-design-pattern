package com.handbook.microservices.aggregation.fields;

import com.handbook.microservices.aggregation.core.SimCall;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** GraphQL-style: the client names the fields it needs; only the backends that own those fields are called. */
public final class FieldDrivenAggregator {

    private final Map<String, String> fieldOwner; // field -> backend
    private final Map<String, SimCall> backends;

    public FieldDrivenAggregator(Map<String, String> fieldOwner, Map<String, SimCall> backends) {
        this.fieldOwner = fieldOwner;
        this.backends = backends;
    }

    public record Result(Map<String, Object> data, Set<String> backendsCalled, long latencyMs) { }

    public Result query(List<String> fields) {
        Set<String> needed = new LinkedHashSet<>();
        for (String f : fields) {
            needed.add(fieldOwner.get(f)); // (1)!
        }
        long latency = needed.stream().mapToLong(b -> backends.get(b).latencyMs()).max().orElse(0);
        Map<String, Object> data = new LinkedHashMap<>();
        for (String f : fields) {
            data.put(f, backends.get(fieldOwner.get(f)).result().get(f));
        }
        return new Result(data, needed, latency);
    }
}
