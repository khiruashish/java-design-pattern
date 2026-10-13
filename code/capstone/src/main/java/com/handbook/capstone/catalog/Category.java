package com.handbook.capstone.catalog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * [Flyweight] Thousands of products share a handful of category objects (name, tax rate, whether
 * it's perishable). Instances are cached and immutable; products only hold a reference.
 */
public final class Category {

    private static final Map<String, Category> CACHE = new ConcurrentHashMap<>();

    private final String name;
    private final int gstPercent;
    private final boolean perishable;

    private Category(String name, int gstPercent, boolean perishable) {
        this.name = name;
        this.gstPercent = gstPercent;
        this.perishable = perishable;
    }

    public static Category of(String name, int gstPercent, boolean perishable) {
        return CACHE.computeIfAbsent(name, n -> new Category(n, gstPercent, perishable));
    }

    public static int cachedInstances() {
        return CACHE.size();
    }

    public String name() {
        return name;
    }

    public int gstPercent() {
        return gstPercent;
    }

    public boolean perishable() {
        return perishable;
    }

    @Override
    public String toString() {
        return name;
    }
}
