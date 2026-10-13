package com.handbook.capstone.ordering;

import com.handbook.capstone.shared.DomainEvents;
import com.handbook.capstone.shared.DomainEvents.OrderPlaced;
import com.handbook.capstone.shared.DomainEvents.OrderStatusChanged;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * [CQRS] The "My orders" screen reads this denormalized view, built from events, instead of
 * querying the orders table. It's rebuildable and safe against redelivered events.
 */
public final class OrderHistoryView {

    private final Map<String, Map<String, String>> byCustomer = new HashMap<>();
    private final Map<String, String> totals = new HashMap<>();

    public OrderHistoryView(DomainEvents events) {
        events.subscribe(OrderPlaced.class, e -> {
            totals.put(e.orderId(), e.total().toString());
            byCustomer.computeIfAbsent(e.customerId(), c -> new LinkedHashMap<>())
                    .putIfAbsent(e.orderId(), e.orderId() + " " + e.total() + " CONFIRMED"); // idempotent upsert
        });
        events.subscribe(OrderStatusChanged.class, e -> byCustomer.computeIfAbsent(e.customerId(), c -> new LinkedHashMap<>())
                .put(e.orderId(), e.orderId() + " " + totals.get(e.orderId()) + " " + e.status()));
    }

    public List<String> myOrders(String customerId) {
        return new ArrayList<>(byCustomer.getOrDefault(customerId, Map.of()).values());
    }
}
