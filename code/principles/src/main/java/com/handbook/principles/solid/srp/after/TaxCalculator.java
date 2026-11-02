package com.handbook.principles.solid.srp.after;

/** Reason to change: the tax rule. */
public class TaxCalculator {
    private final int ratePercent;

    public TaxCalculator(int ratePercent) {
        this.ratePercent = ratePercent;
    }

    public long totalWithTaxCents(Invoice invoice) {
        long subtotal = invoice.subtotalCents();
        return subtotal + subtotal * ratePercent / 100;
    }
}
