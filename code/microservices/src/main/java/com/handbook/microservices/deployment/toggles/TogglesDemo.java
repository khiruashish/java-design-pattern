package com.handbook.microservices.deployment.toggles;

import com.handbook.microservices.deployment.toggles.FeatureToggles.Rule;

import java.util.Set;

public class TogglesDemo {

    public static void main(String[] args) {
        System.out.println("=== Banking: Rolling Out 'Instant Loan Offers' with Feature Toggles ===");
        FeatureToggles toggles = new FeatureToggles();
        toggles.set("instant-loans", new Rule(false, Set.of("employees"), 10)); // code already deployed to everyone

        System.out.println("Employee (dogfooding)  : " + toggles.isOn("instant-loans", "emp-17", "employees"));
        System.out.println("Customers enabled at 10%: " + count(toggles) + " of 10,000");
        System.out.println("cust-123 asks 3 times   : " + toggles.isOn("instant-loans", "cust-123", "retail") + ", "
                + toggles.isOn("instant-loans", "cust-123", "retail") + ", " + toggles.isOn("instant-loans", "cust-123", "retail") + " (sticky)");

        toggles.set("instant-loans", new Rule(false, Set.of("employees"), 50));
        System.out.println("Raised to 50%           : " + count(toggles) + " of 10,000 (everyone from the 10% still has it)");

        toggles.set("instant-loans", new Rule(true, Set.of("employees"), 50)); // credit-risk team spots a scoring issue
        System.out.println("Kill switch flipped     : " + count(toggles) + " of 10,000, employee=" + toggles.isOn("instant-loans", "emp-17", "employees")
                + " (no deploy needed)");
    }

    private static int count(FeatureToggles toggles) {
        int on = 0;
        for (int i = 0; i < 10_000; i++) {
            on += toggles.isOn("instant-loans", "cust-" + i, "retail") ? 1 : 0;
        }
        return on;
    }
}
