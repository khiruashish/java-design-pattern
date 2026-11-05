package com.handbook.structural.flyweight.grocery;

import java.util.HashMap;
import java.util.Map;

/** Flyweight factory keyed by SKU. In production: a cache in front of the catalog service. */
public class Catalog {

    private final Map<String, ProductInfo> bySku = new HashMap<>();
    private int loads;

    public ProductInfo product(String sku) {
        return bySku.computeIfAbsent(sku, s -> {
            loads++;
            return new ProductInfo(s, "Product " + s, "A long marketing description... ".repeat(10),
                    "https://cdn.example.com/" + s + ".jpg", "{\"kcal\":120,\"protein\":4}");
        });
    }

    public int loads() {
        return loads;
    }
}
