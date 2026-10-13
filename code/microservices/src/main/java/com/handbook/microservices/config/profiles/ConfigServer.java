package com.handbook.microservices.config.profiles;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A config server (Spring Cloud Config style) backed by a Git repo of property files.
 * For an application and profile it merges, from lowest to highest precedence:
 * application → application-{profile} → {app} → {app}-{profile}.
 */
public final class ConfigServer {

    public record Resolved(Map<String, String> properties, Map<String, String> sourceOf) {
    }

    private final Map<String, Map<String, String>> files = new LinkedHashMap<>(); // "file name" → properties

    public ConfigServer file(String name, Map<String, String> properties) {
        files.put(name, properties);
        return this;
    }

    public Resolved resolve(String app, String profile) {
        List<String> order = new ArrayList<>(List.of( // (1)!
                "application", "application-" + profile, app, app + "-" + profile));
        Map<String, String> merged = new TreeMap<>();
        Map<String, String> source = new TreeMap<>();
        for (String file : order) {
            files.getOrDefault(file, Map.of()).forEach((k, v) -> { // (2)!
                merged.put(k, v);
                source.put(k, file);
            });
        }
        return new Resolved(merged, source);
    }
}
