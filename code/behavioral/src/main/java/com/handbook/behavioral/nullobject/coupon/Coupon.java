package com.handbook.behavioral.nullobject.coupon;

import java.util.Map;

/** Every cart always has a Coupon: a real one or NONE. Checkout never checks for null. */
public interface Coupon {

    int discountInr(int subtotalInr);

    String label();

    Coupon NONE = new Coupon() { // (1)!
        public int discountInr(int subtotal) { return 0; }
        public String label() { return "no coupon"; }
    };

    record Percent(String code, int percent, int maxInr) implements Coupon {
        public int discountInr(int subtotal) { return Math.min(subtotal * percent / 100, maxInr); }
        public String label() { return code + " (" + percent + "% up to INR " + maxInr + ")"; }
    }

    record Flat(String code, int amountInr, int minOrderInr) implements Coupon {
        public int discountInr(int subtotal) { return subtotal >= minOrderInr ? amountInr : 0; }
        public String label() { return code + " (INR " + amountInr + " off above INR " + minOrderInr + ")"; }
    }

    static Coupon lookup(String code) { // (2)!
        Map<String, Coupon> active = Map.of(
                "FRESH20", new Percent("FRESH20", 20, 100),
                "SAVE50", new Flat("SAVE50", 50, 499));
        return code == null ? NONE : active.getOrDefault(code.toUpperCase(), NONE);
    }
}
