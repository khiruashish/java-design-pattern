package com.handbook.microservices.config.secrets;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * Configuration in Git holds only REFERENCES to secrets ({@code vault:path#key}). At startup the
 * service resolves them from the secret store (Vault, AWS Secrets Manager) into memory, and every
 * printout masks them.
 */
public final class SecretResolver {

    private static final String PREFIX = "vault:";

    private final Function<String, String> secretStore;

    public SecretResolver(Function<String, String> secretStore) {
        this.secretStore = secretStore;
    }

    public Map<String, String> resolve(Map<String, String> config) {
        Map<String, String> resolved = new TreeMap<>();
        config.forEach((k, v) -> resolved.put(k, v.startsWith(PREFIX) ? secretStore.apply(v.substring(PREFIX.length())) : v)); // (1)!
        return resolved;
    }

    public static Map<String, String> masked(Map<String, String> config, Map<String, String> original) {
        Map<String, String> safe = new TreeMap<>();
        config.forEach((k, v) -> safe.put(k, original.get(k).startsWith(PREFIX) ? "******" : v)); // (2)!
        return safe;
    }
}
