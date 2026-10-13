package com.handbook.microservices.config.validation;

import java.util.Map;

public class ConfigValidationDemo {

    public static void main(String[] args) {
        System.out.println("=== Challenge: typed, fail-fast configuration ===");
        System.out.println("valid  -> " + CheckoutConfig.bind(Map.of(
                "checkout.max-items", "100",
                "checkout.payment-timeout", "PT2S",
                "checkout.payment-url", "https://payments.internal")));
        try {
            CheckoutConfig.bind(Map.of(
                    "checkout.max-items", "1000",
                    "checkout.payment-timeout", "2 seconds",
                    "checkout.payment-url", "http://payments.internal"));
        } catch (CheckoutConfig.InvalidConfigException e) {
            System.out.println("broken -> application failed to start: " + e.getMessage());
        }
    }
}
