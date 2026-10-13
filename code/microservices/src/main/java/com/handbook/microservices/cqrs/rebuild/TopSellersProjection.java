package com.handbook.microservices.cqrs.rebuild;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Coding challenge: product asks for a new "top sellers this week" screen months after launch.
 * Because the read models are derived data, we build a new projection and replay the stored
 * event history into it. No migration of the write model is needed.
 */
public final class TopSellersProjection {

    public record OrderPlaced(long sequence, String sku, int qty) {
    }

    private final Map<String, Integer> unitsBySku = new HashMap<>();
    private long lastSequence; // checkpoint: where to resume after a restart

    public void apply(OrderPlaced e) {
        if (e.sequence() <= lastSequence) {
            return; // already applied (replays and redeliveries are safe)
        }
        unitsBySku.merge(e.sku(), e.qty(), Integer::sum);
        lastSequence = e.sequence();
    }

    public List<String> top(int n) {
        return unitsBySku.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
                .limit(n)
                .map(e -> e.getKey() + "=" + e.getValue())
                .toList();
    }

    public long checkpoint() {
        return lastSequence;
    }
}
