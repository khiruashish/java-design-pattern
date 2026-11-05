package com.handbook.structural.composite.ecommerce;

import java.util.List;

public class BundleDemo {

    public static void main(String[] args) {
        System.out.println("=== E-commerce: Nested Product Bundles ===");
        Bundle accessories = new Bundle("Accessories Pack", 5)
                .add(new Product("Wireless Mouse", 799))
                .add(new Product("Mechanical Keyboard", 1_499));

        Bundle homeOffice = new Bundle("Home Office Combo", 10)
                .add(new Product("Laptop 14in", 55_000))
                .add(accessories)
                .add(new Product("Ergonomic Chair", 8_999));

        System.out.println(homeOffice.describe(""));

        System.out.println("\nCart (client treats every item the same way):");
        List<CatalogItem> cart = List.of(homeOffice, new Product("USB-C Cable", 349));
        int total = 0;
        for (CatalogItem item : cart) {
            System.out.printf("  %-18s INR %,7d%n", item.name(), item.priceInr());
            total += item.priceInr();
        }
        System.out.printf("  %-18s INR %,7d%n", "TOTAL", total);
    }
}
