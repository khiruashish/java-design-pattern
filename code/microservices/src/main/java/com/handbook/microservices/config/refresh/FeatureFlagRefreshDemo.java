package com.handbook.microservices.config.refresh;

import java.util.Map;

public class FeatureFlagRefreshDemo {

    public static void main(String[] args) {
        var config = new RefreshableConfig(Map.of("upi.enabled", "true", "upi.max.amount", "100000"));
        config.onChange((key, value) -> System.out.println("    [refresh] " + key + " -> " + value));

        System.out.println("=== Fintech: switching a payment method off without a deploy ===");
        System.out.println("pay ₹500 by UPI   : " + pay(config, 500));
        System.out.println("-- the UPI partner reports an outage; ops flips the flag in Git --");
        config.refresh(Map.of("upi.enabled", "false", "upi.max.amount", "100000"));
        System.out.println("pay ₹500 by UPI   : " + pay(config, 500));
        System.out.println("-- partner recovers, with a temporary lower limit --");
        config.refresh(Map.of("upi.enabled", "true", "upi.max.amount", "20000"));
        System.out.println("pay ₹500 by UPI   : " + pay(config, 500));
        System.out.println("pay ₹50,000 by UPI: " + pay(config, 50_000));
        System.out.println("config version " + config.version() + ", zero restarts");
    }

    private static String pay(RefreshableConfig config, long rupees) {
        if (!Boolean.parseBoolean(config.get("upi.enabled"))) {
            return "UPI temporarily unavailable, please use a card";
        }
        if (rupees > Long.parseLong(config.get("upi.max.amount"))) {
            return "amount above the current UPI limit";
        }
        return "paid";
    }
}
