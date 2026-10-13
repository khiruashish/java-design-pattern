package com.handbook.microservices.config.profiles;

import java.util.Map;

public class ConfigProfilesDemo {

    public static void main(String[] args) {
        var server = new ConfigServer()
                .file("application", Map.of(
                        "logging.level", "INFO",
                        "http.timeout.ms", "2000",
                        "tracing.sample.ratio", "0.1"))
                .file("application-prod", Map.of(
                        "logging.level", "WARN"))
                .file("order-service", Map.of(
                        "order.max.items", "50",
                        "payment.url", "http://payment-service"))
                .file("order-service-prod", Map.of(
                        "order.max.items", "100",
                        "http.timeout.ms", "800"));

        System.out.println("=== Grocery: one config repo, many services and environments ===");
        for (String profile : new String[] {"dev", "prod"}) {
            var resolved = server.resolve("order-service", profile);
            System.out.println("order-service [" + profile + "]");
            resolved.properties().forEach((k, v) ->
                    System.out.printf("  %-22s = %-22s (from %s)%n", k, v, resolved.sourceOf().get(k)));
        }
    }
}
