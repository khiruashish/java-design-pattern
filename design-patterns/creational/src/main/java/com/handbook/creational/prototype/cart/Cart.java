package com.handbook.creational.prototype.cart;

import java.util.ArrayList;
import java.util.List;

/** Challenge solution: "Reorder" copies a past cart; the copy must be independent. */
public class Cart {

    public static final class Line {
        private final String sku;
        private int quantity;

        public Line(String sku, int quantity) {
            this.sku = sku;
            this.quantity = quantity;
        }

        Line copy() {
            return new Line(sku, quantity);
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        @Override
        public String toString() {
            return sku + "x" + quantity;
        }
    }

    private final String customerId;
    private final List<Line> lines = new ArrayList<>();

    public Cart(String customerId) {
        this.customerId = customerId;
    }

    public Cart add(String sku, int quantity) {
        lines.add(new Line(sku, quantity));
        return this;
    }

    public Line line(int index) {
        return lines.get(index);
    }

    public Cart copy() {
        Cart copy = new Cart(customerId);
        lines.forEach(l -> copy.lines.add(l.copy()));
        return copy;
    }

    @Override
    public String toString() {
        return customerId + " " + lines;
    }
}
