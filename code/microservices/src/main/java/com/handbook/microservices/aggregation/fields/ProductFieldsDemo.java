package com.handbook.microservices.aggregation.fields;

import com.handbook.microservices.aggregation.core.SimCall;
import com.handbook.microservices.aggregation.fields.FieldDrivenAggregator.Result;

import java.util.List;
import java.util.Map;

public class ProductFieldsDemo {

    public static void main(String[] args) {
        System.out.println("=== E-commerce: Each Screen Asks Only for the Fields It Shows ===");
        FieldDrivenAggregator gateway = new FieldDrivenAggregator(
                Map.of("name", "catalog", "price", "pricing", "stock", "inventory", "rating", "reviews", "eta", "delivery"),
                Map.of("catalog", SimCall.ok("catalog", 30, Map.of("name", "Noise-cancelling earbuds")),
                        "pricing", SimCall.ok("pricing", 25, Map.of("price", "INR 3,999")),
                        "inventory", SimCall.ok("inventory", 40, Map.of("stock", "In stock")),
                        "reviews", SimCall.ok("reviews", 180, Map.of("rating", "4.3 (2,140)")),
                        "delivery", SimCall.ok("delivery", 70, Map.of("eta", "Tomorrow"))));

        Result listTile = gateway.query(List.of("name", "price"));
        System.out.println("Search tile   " + listTile.data() + " -> called " + listTile.backendsCalled() + ", " + listTile.latencyMs() + " ms");
        Result detail = gateway.query(List.of("name", "price", "stock", "rating", "eta"));
        System.out.println("Product page  -> called " + detail.backendsCalled().size() + " backends, " + detail.latencyMs() + " ms (reviews is the slow one)");
        System.out.println("A fixed 'product' endpoint would make every search tile wait 180 ms for reviews it doesn't show.");
    }
}
