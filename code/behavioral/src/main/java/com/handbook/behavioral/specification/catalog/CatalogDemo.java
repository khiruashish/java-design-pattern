package com.handbook.behavioral.specification.catalog;

import com.handbook.behavioral.specification.catalog.ProductSpecs.Product;
import com.handbook.behavioral.specification.core.Spec;

import java.util.List;

import static com.handbook.behavioral.specification.catalog.ProductSpecs.*;

public class CatalogDemo {

    public static void main(String[] args) {
        System.out.println("=== E-commerce: Search Filters as Specifications ===");
        List<Product> catalog = List.of(
                new Product("Zing Buds 141", "Zing", 1_299, 4.1, 120, true),
                new Product("Pulse Buds VS104", "Pulse", 1_499, 4.6, 0, true),
                new Product("Sonix Wave 200", "Sonix", 2_999, 4.4, 35, false),
                new Product("Pulse Buds X Prime", "Pulse", 1_799, 4.5, 18, true),
                new Product("Aura Buds T300", "Aura", 2_299, 4.7, 60, true));

        // Customer ticked: in stock, under 2000, (Pulse OR rated 4.5+). A second shopper wants COD only.
        Spec<Product> filter = inStock().and(priceAtMost(2_000)).and(brand("Pulse").or(ratingAtLeast(4.5)));
        Spec<Product> codShopper = inStock().and(codAvailable()).and(brand("Sonix").not());

        for (Spec<Product> spec : List.of(filter, codShopper)) {
            System.out.println("Filter: " + spec.describe());
            catalog.stream().filter(spec::isSatisfiedBy).forEach(p -> System.out.println("  - " + p.name() + " INR " + p.priceInr()));
        }
    }
}
