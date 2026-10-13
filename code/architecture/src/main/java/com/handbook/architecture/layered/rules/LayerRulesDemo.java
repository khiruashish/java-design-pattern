package com.handbook.architecture.layered.rules;

import com.handbook.architecture.layered.rules.LayerRules.Dependency;

import java.util.List;

public class LayerRulesDemo {

    public static void main(String[] args) {
        LayerRules rules = new LayerRules().layer("app.web").layer("app.service").layer("app.repository");

        List<Dependency> deps = List.of(
                new Dependency("app.web.OrderController", "app.service.OrderService"),
                new Dependency("app.service.OrderService", "app.repository.OrderRepository"),
                new Dependency("app.web.OrderController", "app.repository.OrderRepository"), // skips a layer
                new Dependency("app.repository.OrderRepository", "app.web.OrderResponse"),   // upward!
                new Dependency("app.service.PricingService", "app.web.Pagination"));         // upward!

        System.out.println("=== Challenge: detect layer violations ===");
        List<String> violations = rules.violations(deps);
        violations.forEach(v -> System.out.println("VIOLATION " + v));
        System.out.println(violations.size() + " violations; skipping a layer downward is allowed in relaxed layering.");
    }
}
