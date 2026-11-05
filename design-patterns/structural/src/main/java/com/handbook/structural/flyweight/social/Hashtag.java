package com.handbook.structural.flyweight.social;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/** Challenge solution: interned, case-insensitive hashtags, safe under concurrency. */
public final class Hashtag {

    private static final ConcurrentMap<String, Hashtag> CACHE = new ConcurrentHashMap<>();

    private final String normalized;

    private Hashtag(String normalized) {
        this.normalized = normalized;
    }

    public static Hashtag of(String raw) {
        String key = raw.strip().toLowerCase(Locale.ROOT);
        if (!key.matches("#[a-z0-9_]{1,50}")) {
            throw new IllegalArgumentException("Not a hashtag: " + raw);
        }
        return CACHE.computeIfAbsent(key, Hashtag::new);
    }

    public static int cached() {
        return CACHE.size();
    }

    @Override
    public String toString() {
        return normalized;
    }
}
