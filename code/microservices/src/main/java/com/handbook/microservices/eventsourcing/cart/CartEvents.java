package com.handbook.microservices.eventsourcing.cart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Cart events, the current cart derived from them, and an insight a CRUD table would have lost. */
public final class CartEvents {

    public sealed interface Event permits ItemAdded, ItemRemoved, CheckedOut {
        String cartId();
    }

    public record ItemAdded(String cartId, String sku) implements Event {
    }

    public record ItemRemoved(String cartId, String sku) implements Event {
    }

    public record CheckedOut(String cartId) implements Event {
    }

    private CartEvents() {
    }

    /** Current state: what a CRUD "cart_items" table would hold. */
    public static Map<String, List<String>> currentCarts(List<Event> log) {
        Map<String, List<String>> carts = new TreeMap<>();
        for (Event e : log) {
            switch (e) {
                case ItemAdded a -> carts.computeIfAbsent(a.cartId(), k -> new ArrayList<>()).add(a.sku());
                case ItemRemoved r -> carts.get(r.cartId()).remove(r.sku());
                case CheckedOut c -> { }
            }
        }
        return carts;
    }

    /** History-based insight: items removed from carts that were later checked out anyway. */
    public static Map<String, Integer> removedBeforeCheckout(List<Event> log) {
        Map<String, List<String>> removedPerCart = new HashMap<>();
        Map<String, Integer> counts = new TreeMap<>();
        for (Event e : log) {
            switch (e) {
                case ItemRemoved r -> removedPerCart.computeIfAbsent(r.cartId(), k -> new ArrayList<>()).add(r.sku());
                case CheckedOut c -> removedPerCart.getOrDefault(c.cartId(), List.of())
                        .forEach(sku -> counts.merge(sku, 1, Integer::sum)); // (1)!
                case ItemAdded a -> { }
            }
        }
        return counts;
    }
}
