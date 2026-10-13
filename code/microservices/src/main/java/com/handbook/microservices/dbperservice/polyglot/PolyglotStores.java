package com.handbook.microservices.dbperservice.polyglot;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Database per service lets each service pick the store that fits its access pattern.
 * These in-memory stand-ins mimic the shape of each real store.
 */
public final class PolyglotStores {

    private PolyglotStores() {
    }

    /** Session service → key-value store with TTL (Redis): lookups by key, data expires. */
    public static final class SessionStore {
        private record Entry(String value, Instant expiresAt) {
        }

        private final Map<String, Entry> data = new HashMap<>();
        private final Clock clock;

        public SessionStore(Clock clock) {
            this.clock = clock;
        }

        public void put(String key, String value, Duration ttl) { // SET key value EX ttl
            data.put(key, new Entry(value, clock.instant().plus(ttl)));
        }

        public String get(String key) {
            Entry e = data.get(key);
            return e == null || !clock.instant().isBefore(e.expiresAt()) ? null : e.value();
        }
    }

    /** Search service → inverted index (Elasticsearch/OpenSearch): term → document IDs. */
    public static final class SearchIndex {
        private final Map<String, Set<String>> index = new TreeMap<>();
        private final Map<String, String> docs = new HashMap<>();

        public void index(String id, String text) {
            docs.put(id, text);
            for (String term : text.toLowerCase().split("\\W+")) {
                index.computeIfAbsent(term, t -> new TreeSet<>()).add(id);
            }
        }

        public List<String> search(String query) { // all terms must match
            return Arrays.stream(query.toLowerCase().split("\\s+"))
                    .map(t -> index.getOrDefault(t, Set.of()))
                    .reduce((a, b) -> {
                        Set<String> both = new TreeSet<>(a);
                        both.retainAll(b);
                        return both;
                    })
                    .orElse(Set.of()).stream().map(docs::get).toList();
        }
    }
}
