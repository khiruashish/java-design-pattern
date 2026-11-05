package com.handbook.behavioral.memento.cart;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Originator with a checkpoint/rollback API, used around risky multi-step changes. */
public class Cart {

    private final Map<String, Integer> items = new LinkedHashMap<>();
    private String coupon;

    public record Checkpoint(Map<String, Integer> items, String coupon) { // (1)!
        public Checkpoint {
            items = Collections.unmodifiableMap(new LinkedHashMap<>(items)); // keeps order, unlike Map.copyOf
        }
    }

    public Checkpoint checkpoint() {
        return new Checkpoint(items, coupon);
    }

    public void rollback(Checkpoint c) {
        items.clear();
        items.putAll(c.items());
        coupon = c.coupon();
    }

    public void add(String sku, int qty) { items.merge(sku, qty, Integer::sum); }

    public void remove(String sku) { items.remove(sku); }

    public void clear() { items.clear(); coupon = null; }

    public void applyCoupon(String code) { coupon = code; }

    public int size() { return items.size(); }

    @Override
    public String toString() {
        return items + (coupon == null ? "" : " coupon=" + coupon);
    }
}
