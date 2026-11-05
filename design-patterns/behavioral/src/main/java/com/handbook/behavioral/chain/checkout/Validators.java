package com.handbook.behavioral.chain.checkout;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Concrete handlers, cheapest checks first. */
public final class Validators {

    private Validators() {
    }

    public static class ServiceableArea extends CheckoutValidator {
        private static final Set<String> PINCODES = Set.of("560034", "560038", "560102");

        protected Optional<String> check(Order o) {
            return PINCODES.contains(o.pincode()) ? Optional.empty()
                    : Optional.of("We don't deliver to " + o.pincode() + " yet");
        }
    }

    public static class StockAvailable extends CheckoutValidator {
        private final Map<String, Integer> stock = Map.of("MILK-1L", 40, "MANGO-1KG", 2);

        protected Optional<String> check(Order o) {
            int available = stock.getOrDefault(o.sku(), 0);
            return o.qty() <= available ? Optional.empty()
                    : Optional.of("Only " + available + " left of " + o.sku());
        }
    }

    public static class CouponValid extends CheckoutValidator {
        protected Optional<String> check(Order o) {
            if (o.coupon() == null) {
                return Optional.empty();
            }
            return o.coupon().equals("FRESH50") && o.amountInr() >= 299 ? Optional.empty()
                    : Optional.of("Coupon " + o.coupon() + " is not valid for this order");
        }
    }

    public static class CodLimit extends CheckoutValidator {
        protected Optional<String> check(Order o) {
            return o.amountInr() <= 5_000 ? Optional.empty()
                    : Optional.of("Cash on delivery is limited to INR 5000");
        }
    }
}
