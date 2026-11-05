package com.handbook.microservices.aggregation.dag;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Coding challenge: some calls need another call's output. Run each call as soon as its inputs are ready. */
public final class DependencyAggregator {

    public record Step(String name, long latencyMs, List<String> dependsOn) { }

    /** @return finish time (virtual ms) of each step when independent steps run in parallel. */
    public static Map<String, Long> schedule(List<Step> steps) {
        Map<String, Long> finish = new LinkedHashMap<>();
        for (Step s : steps) { // steps are listed in a valid (topological) order
            long start = s.dependsOn().stream().mapToLong(finish::get).max().orElse(0);
            finish.put(s.name(), start + s.latencyMs());
        }
        return finish;
    }

    public static long sequential(List<Step> steps) {
        return steps.stream().mapToLong(Step::latencyMs).sum();
    }
}
