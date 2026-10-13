package com.handbook.lld.discount;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;

/**
 * Grocery discount engine LLD. Patterns: Specification (eligibility predicates that combine with
 * and/or), Strategy (how a rule computes its discount), and a policy for combining rules:
 * stackable rules all apply; among exclusive rules only the best one does.
 */
public final class DiscountEngine {

    public record Item(String sku, String category, int qty, long unitPaise) {
        long total() {
            return qty * unitPaise;
        }
    }

    public record Cart(String customerTier, List<Item> items) {
        long subtotal() {
            return items.stream().mapToLong(Item::total).sum();
        }
    }

    /** Specification: is the rule applicable to this cart? */
    public interface Spec extends Predicate<Cart> {
        default Spec and(Spec other) {
            return c -> test(c) && other.test(c);
        }

        static Spec subtotalAtLeast(long paise) {
            return c -> c.subtotal() >= paise;
        }

        static Spec tier(String tier) {
            return c -> c.customerTier().equals(tier);
        }

        static Spec contains(String sku) {
            return c -> c.items().stream().anyMatch(i -> i.sku().equals(sku));
        }
    }

    public record Rule(String name, Spec when, ToLongFunction<Cart> discount, boolean exclusive) {
    }

    public record Applied(String rule, long paise) {
    }

    /** Strategies for common rules. */
    public static ToLongFunction<Cart> percentOffCategory(String category, int percent) {
        return c -> c.items().stream().filter(i -> i.category().equals(category)).mapToLong(Item::total).sum() * percent / 100;
    }

    public static ToLongFunction<Cart> buyXGetYFree(String sku, int x, int y) {
        return c -> c.items().stream().filter(i -> i.sku().equals(sku))
                .mapToLong(i -> (long) (i.qty() / (x + y)) * y * i.unitPaise()).sum();
    }

    public static ToLongFunction<Cart> flat(long paise) {
        return c -> paise;
    }

    private final List<Rule> rules = new ArrayList<>();

    public DiscountEngine add(Rule rule) {
        rules.add(rule);
        return this;
    }

    public List<Applied> apply(Cart cart) {
        List<Applied> applied = new ArrayList<>();
        for (Rule r : rules) {
            if (!r.exclusive() && r.when().test(cart)) {
                long d = r.discount().applyAsLong(cart);
                if (d > 0) {
                    applied.add(new Applied(r.name(), d));
                }
            }
        }
        rules.stream().filter(Rule::exclusive).filter(r -> r.when().test(cart))
                .map(r -> new Applied(r.name(), r.discount().applyAsLong(cart)))
                .filter(a -> a.paise() > 0)
                .max(Comparator.comparingLong(Applied::paise))
                .ifPresent(applied::add); // best exclusive offer only
        return applied;
    }

    public static Map<String, Long> summary(Cart cart, List<Applied> applied) {
        long discount = Math.min(cart.subtotal(), applied.stream().mapToLong(Applied::paise).sum()); // never negative totals
        return Map.of("subtotal", cart.subtotal(), "discount", discount, "total", cart.subtotal() - discount);
    }
}
