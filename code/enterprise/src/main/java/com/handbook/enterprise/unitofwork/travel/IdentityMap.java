package com.handbook.enterprise.unitofwork.travel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/** Ensures each record is loaded once per session: one id -> one in-memory object. */
public final class IdentityMap<K, V> {

    private final Map<K, V> loaded = new HashMap<>();
    private final Function<K, V> loader;

    public IdentityMap(Function<K, V> loader) {
        this.loader = loader;
    }

    public V get(K id) {
        return loaded.computeIfAbsent(id, loader); // (1)!
    }

    public int size() {
        return loaded.size();
    }
}
