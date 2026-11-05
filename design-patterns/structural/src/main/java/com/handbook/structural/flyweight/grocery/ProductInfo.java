package com.handbook.structural.flyweight.grocery;

/** Flyweight: catalog data that is identical in every store. */
public record ProductInfo(String sku, String name, String description, String imageUrl, String nutritionJson) {
}
