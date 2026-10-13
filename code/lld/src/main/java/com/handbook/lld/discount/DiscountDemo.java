package com.handbook.lld.discount;

import com.handbook.lld.discount.DiscountEngine.Cart;
import com.handbook.lld.discount.DiscountEngine.Item;
import com.handbook.lld.discount.DiscountEngine.Rule;
import com.handbook.lld.discount.DiscountEngine.Spec;

import java.util.List;

public class DiscountDemo {

    public static void main(String[] args) {
        var engine = new DiscountEngine()
                .add(new Rule("Mango 2+1 free", Spec.contains("MANGO-1KG"), DiscountEngine.buyXGetYFree("MANGO-1KG", 2, 1), false))
                .add(new Rule("Gold: 10% off dairy", Spec.tier("GOLD"), DiscountEngine.percentOffCategory("dairy", 10), false))
                .add(new Rule("FLAT150 over ₹1,000", Spec.subtotalAtLeast(100_000), DiscountEngine.flat(15_000), true))
                .add(new Rule("SAVE5% over ₹2,000", Spec.subtotalAtLeast(200_000),
                        c -> c.subtotal() * 5 / 100, true));

        System.out.println("=== LLD: Grocery discount engine ===");
        for (Cart cart : List.of(
                new Cart("GOLD", List.of(new Item("MANGO-1KG", "fruit", 3, 39_900), new Item("PANEER-200G", "dairy", 2, 9_000))),
                new Cart("REGULAR", List.of(new Item("RICE-5KG", "staples", 5, 64_900))),
                new Cart("REGULAR", List.of(new Item("MILK-1L", "dairy", 2, 5_600))))) {
            var applied = engine.apply(cart);
            System.out.printf("%s cart, subtotal ₹%,.2f%n", cart.customerTier(), cart.subtotal() / 100.0);
            applied.forEach(a -> System.out.printf("  - %-22s -₹%,.2f%n", a.rule(), a.paise() / 100.0));
            var s = DiscountEngine.summary(cart, applied);
            System.out.printf("  total ₹%,.2f%n", s.get("total") / 100.0);
        }
    }
}
