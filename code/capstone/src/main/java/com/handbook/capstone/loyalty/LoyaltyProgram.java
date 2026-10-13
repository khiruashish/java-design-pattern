package com.handbook.capstone.loyalty;

import com.handbook.capstone.shared.DomainEvents;
import com.handbook.capstone.shared.DomainEvents.OrderPlaced;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Loyalty module: 1 point per ₹100. [Idempotent Consumer]: processed event IDs are remembered. */
public final class LoyaltyProgram {

    private final Map<String, Long> points = new HashMap<>();
    private final Set<String> processed = new HashSet<>(); // a processed_events table in production
    private int duplicatesIgnored;

    public LoyaltyProgram(DomainEvents events) {
        events.subscribe(OrderPlaced.class, e -> {
            if (!processed.add(e.eventId())) {
                duplicatesIgnored++;
                return;
            }
            points.merge(e.customerId(), e.total().paise() / 10_000, Long::sum);
        });
    }

    public long points(String customerId) {
        return points.getOrDefault(customerId, 0L);
    }

    public int duplicatesIgnored() {
        return duplicatesIgnored;
    }
}
