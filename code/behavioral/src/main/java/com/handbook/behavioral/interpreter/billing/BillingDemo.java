package com.handbook.behavioral.interpreter.billing;

import java.util.Map;

public class BillingDemo {

    public static void main(String[] args) {
        System.out.println("=== Telecom: Tariff Formulas Configured by the Pricing Team ===");
        // Stored in the product catalogue, not in code:
        String postpaid = "rental + max(minutes - free_minutes, 0) * per_min + max(data_gb - free_gb, 0) * per_gb";
        String roaming = "min(minutes * intl_rate, cap)";
        Formula bill = FormulaParser.parse(postpaid);
        Formula roam = FormulaParser.parse(roaming);

        Map<String, Double> light = Map.of("rental", 399.0, "minutes", 250.0, "free_minutes", 300.0,
                "per_min", 1.0, "data_gb", 18.0, "free_gb", 20.0, "per_gb", 50.0);
        Map<String, Double> heavy = Map.of("rental", 399.0, "minutes", 420.0, "free_minutes", 300.0,
                "per_min", 1.0, "data_gb", 26.5, "free_gb", 20.0, "per_gb", 50.0);

        System.out.printf("Light user bill : INR %.2f%n", bill.eval(light));
        System.out.printf("Heavy user bill : INR %.2f%n", bill.eval(heavy));
        System.out.printf("Roaming 30 min  : INR %.2f%n", roam.eval(Map.of("minutes", 30.0, "intl_rate", 25.0, "cap", 999.0)));
        System.out.printf("Roaming 90 min  : INR %.2f (capped)%n", roam.eval(Map.of("minutes", 90.0, "intl_rate", 25.0, "cap", 999.0)));
    }
}
