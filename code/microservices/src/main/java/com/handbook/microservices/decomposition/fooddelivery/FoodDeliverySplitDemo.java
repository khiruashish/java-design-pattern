package com.handbook.microservices.decomposition.fooddelivery;

import com.handbook.microservices.decomposition.healthcare.BoundaryChecker;
import com.handbook.microservices.decomposition.healthcare.BoundaryChecker.UseCase;

import java.util.List;
import java.util.Map;
import java.util.Set;

/** Challenge solution: a food-delivery split where no use case writes across services. */
public class FoodDeliverySplitDemo {

    public static void main(String[] args) {
        List<UseCase> useCases = List.of(
                new UseCase("Update menu", Set.of("menus", "dishes"), Set.of("restaurants")),
                new UseCase("Place order", Set.of("orders", "order_lines"), Set.of("dishes", "customers")),
                new UseCase("Assign rider", Set.of("assignments"), Set.of("riders", "orders")),
                new UseCase("Rider location ping", Set.of("rider_locations"), Set.of()),
                new UseCase("Capture payment", Set.of("payments"), Set.of("orders")));

        Map<String, String> split = Map.ofEntries(
                Map.entry("restaurants", "Restaurant"), Map.entry("menus", "Restaurant"), Map.entry("dishes", "Restaurant"),
                Map.entry("customers", "Customer"),
                Map.entry("orders", "Ordering"), Map.entry("order_lines", "Ordering"),
                Map.entry("riders", "Delivery"), Map.entry("assignments", "Delivery"), Map.entry("rider_locations", "Delivery"),
                Map.entry("payments", "Payments"));

        BoundaryChecker.check(split, useCases).forEach((uc, v) -> System.out.printf("%-20s %s%n", uc, v));
    }
}
