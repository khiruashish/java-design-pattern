package com.handbook.creational.prototype.ecommerce;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A product listing with lots of shared data (description, images, attributes).
 * Variants (size/colour) are created by copying a base listing and changing a few fields.
 */
public class ProductListing {

    private String sku;
    private final String title;
    private final String description;
    private final List<String> imageUrls;
    private final Map<String, String> attributes;
    private int priceInr;

    public ProductListing(String sku, String title, String description,
                          List<String> imageUrls, Map<String, String> attributes, int priceInr) {
        this.sku = sku;
        this.title = title;
        this.description = description;
        this.imageUrls = new ArrayList<>(imageUrls);
        this.attributes = new LinkedHashMap<>(attributes);
        this.priceInr = priceInr;
    }

    /** Copy constructor: the preferred way to implement Prototype in Java. */
    public ProductListing(ProductListing other) { // (1)!
        this(other.sku, other.title, other.description,
                other.imageUrls, other.attributes, other.priceInr); // (2)!
    }

    public ProductListing variant(String sku, String attribute, String value, int priceInr) {
        ProductListing copy = new ProductListing(this);
        copy.sku = sku;
        copy.attributes.put(attribute, value);
        copy.priceInr = priceInr;
        return copy;
    }

    @Override
    public String toString() {
        return "%-12s %-18s INR %4d %s images=%d".formatted(sku, title, priceInr, attributes, imageUrls.size());
    }
}
