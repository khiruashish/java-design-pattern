package com.handbook.structural.decorator.pricing;

/** Component. Amounts in paise to avoid floating-point money. */
@FunctionalInterface
public interface PriceCalculator {

    long price(long subtotalPaise);

    static PriceCalculator base() {
        return subtotal -> subtotal;
    }

    // ---- decorators as small functions that wrap another calculator ----

    default PriceCalculator thenMemberDiscount(int percent) { // (1)!
        return subtotal -> {
            long p = price(subtotal);
            return p - p * percent / 100;
        };
    }

    default PriceCalculator thenFlatOff(long paiseOff, long minimumPaise) {
        return subtotal -> {
            long p = price(subtotal);
            return p >= minimumPaise ? p - paiseOff : p;
        };
    }

    default PriceCalculator thenGst(int percent) {
        return subtotal -> {
            long p = price(subtotal);
            return p + Math.round(p * percent / 100.0);
        };
    }
}
