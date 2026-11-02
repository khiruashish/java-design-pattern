import java.util.*;

/** Problem: checkout applies one of several discount rules chosen at runtime; rules must be pluggable and testable. */
public class StrategyPricing {

    record Cart(String customerTier, List<Long> itemCents) {
        long subtotal() { return itemCents.stream().mapToLong(Long::longValue).sum(); }
    }

    /** The strategy interface: one method, so any lambda is a strategy. */
    @FunctionalInterface
    interface DiscountStrategy {
        long discountCents(Cart cart);

        static DiscountStrategy none() { return cart -> 0; }
        static DiscountStrategy percent(int pct) { return cart -> cart.subtotal() * pct / 100; }
        static DiscountStrategy flatAbove(long thresholdCents, long offCents) {
            return cart -> cart.subtotal() >= thresholdCents ? offCents : 0;
        }
        static DiscountStrategy buyNGetCheapestFree(int n) {
            return cart -> cart.itemCents().size() >= n ? Collections.min(cart.itemCents()) : 0;
        }

        /** Composite strategy: customer gets the best of several rules. */
        static DiscountStrategy bestOf(DiscountStrategy... strategies) {
            return cart -> Arrays.stream(strategies).mapToLong(s -> s.discountCents(cart)).max().orElse(0);
        }
    }

    /** The context: knows nothing about specific rules. */
    static final class Checkout {
        private final Map<String, DiscountStrategy> byTier;

        Checkout(Map<String, DiscountStrategy> byTier) { this.byTier = Map.copyOf(byTier); }

        long total(Cart cart) {
            DiscountStrategy strategy = byTier.getOrDefault(cart.customerTier(), DiscountStrategy.none());
            long discount = Math.min(strategy.discountCents(cart), cart.subtotal());   // never negative totals
            return cart.subtotal() - discount;
        }
    }

    public static void main(String[] args) {
        Checkout checkout = new Checkout(Map.of(
                "GOLD", DiscountStrategy.percent(10),
                "SILVER", DiscountStrategy.flatAbove(5_000, 500),
                "PLATINUM", DiscountStrategy.bestOf(DiscountStrategy.percent(15), DiscountStrategy.buyNGetCheapestFree(3))));

        List<Long> items = List.of(2_000L, 3_000L, 1_500L);
        for (String tier : List.of("GUEST", "SILVER", "GOLD", "PLATINUM")) {
            System.out.printf("%-8s subtotal=6500 total=%d%n", tier, checkout.total(new Cart(tier, items)));
        }
        check(checkout.total(new Cart("GUEST", items)) == 6_500, "no strategy");
        check(checkout.total(new Cart("SILVER", items)) == 6_000, "flat");
        check(checkout.total(new Cart("GOLD", items)) == 5_850, "percent");
        check(checkout.total(new Cart("PLATINUM", items)) == 5_000, "best of 15% (975) and cheapest free (1500)");
        System.out.println("PASS");
    }

    static void check(boolean ok, String what) { if (!ok) { System.out.println("FAILED: " + what); System.exit(1); } }
}
