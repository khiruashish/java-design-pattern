package com.handbook.structural.decorator.caching;

import java.util.List;

public class CachingDemo {

    public static void main(String[] args) {
        ProductCatalog slowDb = sku -> {
            System.out.println("  (db query for " + sku + ")");
            return sku + ": Salted Butter 500g, INR 285";
        };
        CachingCatalog catalog = new CachingCatalog(slowDb);

        for (String sku : List.of("BUTTER-500", "BUTTER-500", "BUTTER-500")) {
            System.out.println(catalog.describe(sku));
        }
        catalog.evict("BUTTER-500"); // price changed
        System.out.println(catalog.describe("BUTTER-500"));
        System.out.println(catalog.stats());
    }
}
