package com.handbook.behavioral.specification.catalog;

import com.handbook.behavioral.specification.core.Spec;

/** Reusable, named rules about products, the building blocks of every filter. */
public final class ProductSpecs {

    private ProductSpecs() {
    }

    public record Product(String name, String brand, int priceInr, double rating, int stock, boolean codAllowed) {
    }

    public static Spec<Product> inStock() {
        return Spec.of("in stock", p -> p.stock() > 0);
    }

    public static Spec<Product> priceAtMost(int max) {
        return Spec.of("price <= " + max, p -> p.priceInr() <= max);
    }

    public static Spec<Product> brand(String brand) {
        return Spec.of("brand = " + brand, p -> p.brand().equalsIgnoreCase(brand));
    }

    public static Spec<Product> ratingAtLeast(double r) {
        return Spec.of("rating >= " + r, p -> p.rating() >= r);
    }

    public static Spec<Product> codAvailable() {
        return Spec.of("COD available", Product::codAllowed);
    }
}
