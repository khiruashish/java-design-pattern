package com.handbook.capstone.pricing;

import com.handbook.capstone.cart.Cart;
import com.handbook.capstone.shared.Money;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * [Specification] decides whether a promotion applies; [Strategy] decides how much it's worth.
 * Promotions are data: marketing adds new ones without changing the engine.
 */
public final class Pricing {

    public record Promotion(String code, Predicate<Cart> eligible, Function<Cart, Money> discount) {
    }

    public record Breakdown(Money subtotal, List<String> applied, Money discount, Money gst, Money deliveryFee, Money total) {
        @Override
        public String toString() {
            return "subtotal " + subtotal + " - discount " + discount + " " + applied + " + GST " + gst
                    + " + delivery " + deliveryFee + " = " + total;
        }
    }

    /** Specifications. */
    public static Predicate<Cart> minSubtotal(Money min) {
        return c -> c.subtotal().compareTo(min) >= 0;
    }

    public static Predicate<Cart> containsCategory(String category) {
        return c -> c.lines().values().stream().anyMatch(l -> l.product().category().name().equals(category));
    }

    /** Strategies. */
    public static Function<Cart, Money> percentOffCategory(String category, int pct, Money cap) {
        return c -> {
            Money base = c.lines().values().stream().filter(l -> l.product().category().name().equals(category))
                    .map(Cart.Line::total).reduce(Money.ZERO, Money::plus);
            Money d = base.percent(pct);
            return d.compareTo(cap) > 0 ? cap : d;
        };
    }

    public static Function<Cart, Money> flat(Money amount) {
        return c -> amount;
    }

    private final List<Promotion> promotions = new ArrayList<>();
    private final Money freeDeliveryAbove;
    private final Money deliveryFee;

    public Pricing(Money freeDeliveryAbove, Money deliveryFee) {
        this.freeDeliveryAbove = freeDeliveryAbove;
        this.deliveryFee = deliveryFee;
    }

    public Pricing add(Promotion p) {
        promotions.add(p);
        return this;
    }

    public Breakdown price(Cart cart, String couponCode) {
        Money subtotal = cart.subtotal();
        List<String> applied = new ArrayList<>();
        Money discount = Money.ZERO;
        for (Promotion p : promotions) {
            if (p.code().equalsIgnoreCase(couponCode) && p.eligible().test(cart)) {
                discount = discount.plus(p.discount().apply(cart));
                applied.add(p.code());
            }
        }
        Money gst = Money.ZERO;
        for (Cart.Line l : cart.orderedLines()) {
            gst = gst.plus(l.total().percent(l.product().category().gstPercent()));
        }
        Money afterDiscount = subtotal.minus(discount);
        Money delivery = afterDiscount.compareTo(freeDeliveryAbove) >= 0 ? Money.ZERO : deliveryFee;
        return new Breakdown(subtotal, applied, discount, gst, delivery, afterDiscount.plus(gst).plus(delivery));
    }
}
