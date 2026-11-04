package com.handbook.testing.contract.broker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** The "can I deploy?" question: is this provider version verified against every consumer version in that environment? */
public final class ContractBroker {

    public record Verification(String consumer, String consumerVersion, String provider, String providerVersion, boolean passed) { }

    private final List<Verification> results = new ArrayList<>();
    private final Map<String, Map<String, String>> deployed = new HashMap<>(); // env -> app -> version

    public void record(Verification v) {
        results.add(v);
    }

    public void deployed(String env, String app, String version) {
        deployed.computeIfAbsent(env, e -> new HashMap<>()).put(app, version);
    }

    public String canIDeploy(String provider, String version, String env, List<String> consumers) {
        List<String> blockers = new ArrayList<>();
        for (String consumer : consumers) {
            String consumerVersion = deployed.getOrDefault(env, Map.of()).get(consumer); // (1)!
            boolean ok = results.stream().anyMatch(r -> r.consumer().equals(consumer) && r.consumerVersion().equals(consumerVersion)
                    && r.provider().equals(provider) && r.providerVersion().equals(version) && r.passed());
            if (!ok) {
                blockers.add(consumer + "@" + consumerVersion);
            }
        }
        return blockers.isEmpty() ? "YES" : "NO, not verified against " + blockers; // (2)!
    }
}
