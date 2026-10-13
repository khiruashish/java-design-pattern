package com.handbook.microservices.strangler.grocery;

import com.handbook.microservices.support.Http.Request;
import com.handbook.microservices.support.Http.Response;

import java.util.List;
import java.util.Map;

public class StranglerDemo {

    public static void main(String[] args) {
        var router = new StranglerRouter(r -> Response.of(200, "monolith"));
        List<String> paths = List.of("/catalog/MANGO", "/cart", "/orders/9", "/invoices/9");

        System.out.println("=== Grocery: strangling the monolith one route at a time ===");
        print("Day 0   (all legacy)            ", router, paths);

        router.migrate("/catalog", r -> Response.of(200, "catalog-svc"));
        print("Month 2 (catalog extracted)     ", router, paths);

        router.migrate("/cart", r -> Response.of(200, "cart-svc"));
        router.migrate("/orders", r -> Response.of(200, "order-svc"));
        print("Month 5 (cart, orders)          ", router, paths);

        router.rollback("/orders"); // bug found in the new order service
        print("Month 5 (orders rolled back)    ", router, paths);

        router.migrate("/orders", r -> Response.of(200, "order-svc"));
        router.migrate("/invoices", r -> Response.of(200, "billing-svc"));
        print("Month 9 (monolith retired)      ", router, paths);
    }

    private static void print(String label, StranglerRouter router, List<String> paths) {
        System.out.println(label + paths.stream()
                .map(p -> p.split("/")[1] + "->" + router.handle(Request.get(p, Map.of())).body())
                .toList());
    }
}
