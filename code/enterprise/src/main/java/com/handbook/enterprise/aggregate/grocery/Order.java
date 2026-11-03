package com.handbook.enterprise.aggregate.grocery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** Aggregate root. The ONLY way to change lines is through Order, so its rules always hold. */
public final class Order {

    public enum Status { OPEN, PACKED }

    /** Entity inside the aggregate: identified by SKU within this order, never shared. */
    public static final class Line {
        private final String sku;
        private final long unitPaise;
        private int qty;

        private Line(String sku, long unitPaise, int qty) { // (1)!
            this.sku = sku;
            this.unitPaise = unitPaise;
            this.qty = qty;
        }

        public String sku() { return sku; }
        public int qty() { return qty; }
        public long subtotal() { return unitPaise * qty; }
        public String toString() { return sku + " x" + qty; }
    }

    static final int FLASH_SALE_LIMIT_PER_SKU = 4;
    static final long MAX_ORDER_PAISE = 2_000_000; // INR 20,000 cash-on-delivery cap

    private final String id;
    private final String customerId; // (2)!
    private final List<Line> lines = new ArrayList<>();
    private Status status = Status.OPEN;

    public Order(String id, String customerId) {
        this.id = Objects.requireNonNull(id);
        this.customerId = Objects.requireNonNull(customerId);
    }

    public void add(String sku, long unitPaise, int qty) { // (3)!
        if (status != Status.OPEN) {
            throw new IllegalStateException("order " + id + " is " + status + "; cannot add " + sku);
        }
        Line line = lines.stream().filter(l -> l.sku.equals(sku)).findFirst().orElse(null);
        int newQty = (line == null ? 0 : line.qty) + qty;
        if (newQty > FLASH_SALE_LIMIT_PER_SKU) {
            throw new IllegalArgumentException("max " + FLASH_SALE_LIMIT_PER_SKU + " of " + sku + " per order");
        }
        if (total() + unitPaise * qty > MAX_ORDER_PAISE) {
            throw new IllegalArgumentException("order total would exceed INR 20,000 COD cap");
        }
        if (line == null) {
            lines.add(new Line(sku, unitPaise, qty));
        } else {
            line.qty = newQty;
        }
    }

    public void markPacked() {
        if (lines.isEmpty()) {
            throw new IllegalStateException("cannot pack an empty order");
        }
        status = Status.PACKED;
    }

    public long total() { return lines.stream().mapToLong(Line::subtotal).sum(); }
    public List<Line> lines() { return Collections.unmodifiableList(lines); } // (4)!
    public Status status() { return status; }
    public String customerId() { return customerId; }
}
