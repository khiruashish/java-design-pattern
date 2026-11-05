package com.handbook.structural.decorator.pricing;

public class PricingDemo {

    public static void main(String[] args) {
        System.out.println("=== Grocery: Pricing Rules as Decorators ===");
        long cart = 1_000_00; // INR 1000.00

        PriceCalculator correct = PriceCalculator.base()
                .thenMemberDiscount(10)        // INR 1000 -> 900
                .thenFlatOff(100_00, 500_00)   // festival: INR 100 off above INR 500 -> 800
                .thenGst(5);                   // tax on the discounted price -> 840

        PriceCalculator wrongOrder = PriceCalculator.base()
                .thenGst(5)                    // tax first -> 1050
                .thenMemberDiscount(10)        // -> 945
                .thenFlatOff(100_00, 500_00);  // -> 845

        PriceCalculator plain = PriceCalculator.base().thenGst(5);

        System.out.println("No offers           : INR " + inr(plain.price(cart)));
        System.out.println("Discounts, then GST : INR " + inr(correct.price(cart)));
        System.out.println("GST, then discounts : INR " + inr(wrongOrder.price(cart)) + "  <- same rules, different order");
    }

    private static String inr(long paise) {
        return "%d.%02d".formatted(paise / 100, paise % 100);
    }
}
