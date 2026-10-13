package com.handbook.behavioral.interpreter.promo;

import java.util.List;
import java.util.Map;

public class PromoDemo {

    public static void main(String[] args) {
        System.out.println("=== E-commerce: Promo Rules Written by Marketing ===");
        String text = "cart_total >= 999 AND (category == 'electronics' OR member_tier == 'gold') AND NOT city == 'Leh'";
        Rule rule = RuleParser.parse(text);
        System.out.println("Rule text : " + text);
        System.out.println("Parsed AST: " + rule);

        List<Map<String, Object>> carts = List.of(
                Map.of("cart_total", 1499, "category", "electronics", "member_tier", "silver", "city", "Pune"),
                Map.of("cart_total", 1200, "category", "fashion", "member_tier", "gold", "city", "Delhi"),
                Map.of("cart_total", 650, "category", "electronics", "member_tier", "gold", "city", "Pune"),
                Map.of("cart_total", 5000, "category", "electronics", "member_tier", "gold", "city", "Leh"));
        for (Map<String, Object> cart : carts) {
            System.out.printf("total=%-5s category=%-12s tier=%-7s city=%-6s -> %s%n",
                    cart.get("cart_total"), cart.get("category"), cart.get("member_tier"), cart.get("city"),
                    rule.matches(cart) ? "OFFER APPLIES" : "no offer");
        }
        try {
            RuleParser.parse("cart_total >= ");
        } catch (IllegalArgumentException e) {
            System.out.println("Bad rule rejected: " + e.getMessage());
        }
    }
}
