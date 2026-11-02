package com.handbook.principles.solid.srp;

/** One line on an invoice. Money is held in minor units (cents) to avoid floating-point errors. */
public record LineItem(String description, int quantity, long unitPriceCents) {
    public long totalCents() {
        return quantity * unitPriceCents;
    }
}
