package com.handbook.creational.builder.grocery;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Immutable order. The only way to create one is through {@link Builder}. */
public final class GroceryOrder {

    public record Item(String sku, int quantity, int unitPriceInr) {
        public int lineTotal() {
            return quantity * unitPriceInr;
        }
    }

    public enum Slot { EXPRESS_10_MIN, TODAY_6_TO_8_PM, TOMORROW_MORNING }

    private final String customerId;
    private final String address;
    private final List<Item> items;
    private final Slot slot;
    private final String couponCode;   // optional
    private final String instructions; // optional
    private final boolean contactless;

    private GroceryOrder(Builder b) { // (1)!
        this.customerId = b.customerId;
        this.address = b.address;
        this.items = List.copyOf(b.items); // (2)!
        this.slot = b.slot;
        this.couponCode = b.couponCode;
        this.instructions = b.instructions;
        this.contactless = b.contactless;
    }

    public static Builder builder(String customerId, String address) { // (3)!
        return new Builder(customerId, address);
    }

    public int subtotalInr() {
        return items.stream().mapToInt(Item::lineTotal).sum();
    }

    public List<Item> items() {
        return items;
    }

    @Override
    public String toString() {
        return "Order[customer=%s, items=%d, subtotal=INR %d, slot=%s, coupon=%s, contactless=%s, note=%s]"
                .formatted(customerId, items.size(), subtotalInr(), slot,
                        couponCode == null ? "-" : couponCode, contactless,
                        instructions == null ? "-" : '"' + instructions + '"');
    }

    public static final class Builder {
        private final String customerId;
        private final String address;
        private final List<Item> items = new ArrayList<>();
        private Slot slot = Slot.TODAY_6_TO_8_PM; // sensible default
        private String couponCode;
        private String instructions;
        private boolean contactless;

        private Builder(String customerId, String address) {
            this.customerId = Objects.requireNonNull(customerId, "customerId");
            this.address = Objects.requireNonNull(address, "address");
        }

        public Builder item(String sku, int quantity, int unitPriceInr) {
            items.add(new Item(sku, quantity, unitPriceInr));
            return this; // (4)!
        }

        public Builder slot(Slot slot) {
            this.slot = slot;
            return this;
        }

        public Builder coupon(String couponCode) {
            this.couponCode = couponCode;
            return this;
        }

        public Builder instructions(String instructions) {
            this.instructions = instructions;
            return this;
        }

        public Builder contactless() {
            this.contactless = true;
            return this;
        }

        public GroceryOrder build() { // (5)!
            if (items.isEmpty()) {
                throw new IllegalStateException("Order needs at least one item");
            }
            GroceryOrder order = new GroceryOrder(this);
            if (slot == Slot.EXPRESS_10_MIN && order.subtotalInr() > 2_000) {
                throw new IllegalStateException("Express delivery is limited to orders up to INR 2000");
            }
            return order;
        }
    }
}
