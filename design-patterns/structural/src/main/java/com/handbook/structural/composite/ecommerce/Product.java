package com.handbook.structural.composite.ecommerce;

/** Leaf. */
public record Product(String name, int priceInr) implements CatalogItem {

    @Override
    public String describe(String indent) {
        return "%s- %s: INR %,d".formatted(indent, name, priceInr);
    }
}
