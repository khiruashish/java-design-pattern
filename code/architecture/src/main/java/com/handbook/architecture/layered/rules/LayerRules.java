package com.handbook.architecture.layered.rules;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Coding challenge: a tiny version of what ArchUnit's layeredArchitecture() checks. Layers are
 * ordered top to bottom; a class may depend only on its own layer or the layers below it.
 */
public final class LayerRules {

    public record Dependency(String fromClass, String toClass) {
    }

    private final Map<String, Integer> layerRank = new LinkedHashMap<>(); // package prefix → rank

    public LayerRules layer(String packagePrefix) {
        layerRank.put(packagePrefix, layerRank.size());
        return this;
    }

    public List<String> violations(List<Dependency> dependencies) {
        List<String> violations = new ArrayList<>();
        for (Dependency d : dependencies) {
            Integer from = rank(d.fromClass());
            Integer to = rank(d.toClass());
            if (from != null && to != null && to < from) {
                violations.add(simple(d.fromClass()) + " -> " + simple(d.toClass())
                        + " (lower layer depends on a higher one)");
            }
        }
        return violations;
    }

    private Integer rank(String className) {
        return layerRank.entrySet().stream()
                .filter(e -> className.startsWith(e.getKey()))
                .map(Map.Entry::getValue)
                .findFirst().orElse(null);
    }

    private static String simple(String className) {
        return className.substring(className.lastIndexOf('.', className.lastIndexOf('.') - 1) + 1);
    }
}
