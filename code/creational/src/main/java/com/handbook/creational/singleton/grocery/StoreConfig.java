package com.handbook.creational.singleton.grocery;

import java.util.Map;

/**
 * Application-wide store configuration, loaded exactly once.
 * The enum form is the simplest thread-safe Singleton in Java and is
 * immune to reflection and serialization attacks.
 */
public enum StoreConfig {
    INSTANCE; // (1)!

    private final Map<String, String> settings;

    StoreConfig() { // (2)!
        System.out.println("[StoreConfig] Loading configuration (runs once)...");
        // In production this would come from a file, Spring Cloud Config or a DB.
        this.settings = Map.of(
                "delivery.fee.inr", "30",
                "free.delivery.above.inr", "499",
                "feature.express.delivery", "true");
    }

    public int getInt(String key) {
        String value = settings.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Unknown config key: " + key);
        }
        return Integer.parseInt(value);
    }

    public boolean isEnabled(String feature) {
        return Boolean.parseBoolean(settings.getOrDefault("feature." + feature, "false"));
    }
}
