package com.handbook.microservices.config.refresh;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * Holds the current configuration and notifies listeners when a refresh changes a value, like
 * Spring Cloud Bus broadcasting a refresh to every instance and @RefreshScope beans rebinding.
 */
public final class RefreshableConfig {

    private final Map<String, String> values = new ConcurrentHashMap<>();
    private final List<BiConsumer<String, String>> listeners = new ArrayList<>();
    private long version;

    public RefreshableConfig(Map<String, String> initial) {
        values.putAll(initial);
    }

    public String get(String key) {
        return values.get(key);
    }

    public void onChange(BiConsumer<String, String> listener) {
        listeners.add(listener);
    }

    /** Called when the config server reports a new version (webhook from Git → bus → instance). */
    public synchronized void refresh(Map<String, String> latest) { // (1)!
        version++;
        new TreeMap<>(latest).forEach((k, v) -> {
            if (!v.equals(values.put(k, v))) { // (2)!
                listeners.forEach(l -> l.accept(k, v));
            }
        });
    }

    public synchronized long version() {
        return version;
    }
}
