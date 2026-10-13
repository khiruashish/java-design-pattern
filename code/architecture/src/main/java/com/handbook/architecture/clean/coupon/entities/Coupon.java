package com.handbook.architecture.clean.coupon.entities;

import java.time.LocalDate;

/**
 * Entity (innermost circle): enterprise-wide business rules that would exist even without this app.
 * No dependencies on anything outside this circle.
 */
public record Coupon(String code, int percentOff, long minSubtotalPaise, long maxDiscountPaise, LocalDate expires) {

    public sealed interface Outcome permits Discount, NotApplicable {
    }

    public record Discount(long paise) implements Outcome {
    }

    public record NotApplicable(String reason) implements Outcome {
    }

    public Outcome evaluate(long subtotalPaise, LocalDate today) {
        if (today.isAfter(expires)) {
            return new NotApplicable("coupon expired on " + expires);
        }
        if (subtotalPaise < minSubtotalPaise) {
            return new NotApplicable("add ₹" + (minSubtotalPaise - subtotalPaise) / 100 + " more to use " + code);
        }
        return new Discount(Math.min(maxDiscountPaise, subtotalPaise * percentOff / 100));
    }
}
