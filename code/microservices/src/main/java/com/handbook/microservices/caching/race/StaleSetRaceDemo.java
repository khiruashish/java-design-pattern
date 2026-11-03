package com.handbook.microservices.caching.race;

import java.util.HashMap;
import java.util.Map;

/** Coding challenge: a slow reader puts a stale value into the cache after a writer invalidated it. */
public class StaleSetRaceDemo {

    record Versioned(long pricePaise, long version) { }

    static final Map<String, Versioned> DB = new HashMap<>();
    static final Map<String, Versioned> CACHE = new HashMap<>();

    /** Naive set: whatever the reader loaded wins. */
    static void naiveSet(String key, Versioned v) {
        CACHE.put(key, v);
    }

    /** Fix: only replace if the incoming version is newer than what the cache saw last (like SET with a version check). */
    static final Map<String, Long> HIGHEST_SEEN = new HashMap<>();

    static void versionedSet(String key, Versioned v) {
        long highest = HIGHEST_SEEN.getOrDefault(key, -1L);
        if (v.version() >= highest) {
            CACHE.put(key, v);
            HIGHEST_SEEN.put(key, v.version());
        }
    }

    static void invalidate(String key, long newVersion) {
        CACHE.remove(key);
        HIGHEST_SEEN.merge(key, newVersion, Math::max);
    }

    static Versioned run(boolean versioned) {
        DB.put("ONION", new Versioned(4_000, 1));
        CACHE.clear();
        HIGHEST_SEEN.clear();
        Versioned readerLoaded = DB.get("ONION");          // 1. reader misses, loads v1 (then pauses: GC, slow network)
        DB.put("ONION", new Versioned(9_000, 2));          // 2. writer updates DB to v2
        invalidate("ONION", 2);                             // 3. writer deletes cache key
        if (versioned) {
            versionedSet("ONION", readerLoaded);            // 4. reader resumes and sets v1
        } else {
            naiveSet("ONION", readerLoaded);
        }
        return CACHE.get("ONION");
    }

    public static void main(String[] args) {
        System.out.println("=== Challenge: The Cache-Aside Stale-Set Race ===");
        System.out.println("Naive set     -> cache holds " + run(false) + " while DB has v2 (stale until TTL)");
        System.out.println("Versioned set -> cache holds " + run(true) + " (stale write ignored; next read loads v2)");
    }
}
