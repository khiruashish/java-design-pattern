package com.handbook.testing.testdata.grocery;

import com.handbook.testing.testdata.grocery.Order.Line;

import java.util.ArrayList;
import java.util.List;

/** Test Data Builder: every field has a sensible default; tests override only what matters to them. */
public final class OrderBuilder {

    private String id = "ORD-1";
    private String customerId = "CUST-1";
    private String pincode = "560034";
    private String slot = "Today 18:00-19:00";
    private String paymentMethod = "UPI";
    private boolean express = false;
    private boolean firstOrder = false;
    private final List<Line> lines = new ArrayList<>(List.of(new Line("MILK-1L", 1, 6_800))); // (1)!
    private String coupon = null;

    public static OrderBuilder anOrder() { // (2)!
        return new OrderBuilder();
    }

    public OrderBuilder express() { this.express = true; return this; }

    public OrderBuilder firstOrder() { this.firstOrder = true; return this; }

    public OrderBuilder withSubtotal(long paise) { // (3)!
        lines.clear();
        lines.add(new Line("BASKET", 1, paise));
        return this;
    }

    public OrderBuilder withCoupon(String code) { this.coupon = code; return this; }

    public Order build() {
        return new Order(id, customerId, pincode, slot, paymentMethod, express, firstOrder, List.copyOf(lines), coupon);
    }
}
