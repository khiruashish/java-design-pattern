package com.handbook.structural.composite.ecommerce;

import java.util.ArrayList;
import java.util.List;

/** Composite: contains products AND other bundles, and is priced the same way. */
public final class Bundle implements CatalogItem {

    private final String name;
    private final int discountPercent;
    private final List<CatalogItem> children = new ArrayList<>(); // (1)!

    public Bundle(String name, int discountPercent) {
        this.name = name;
        this.discountPercent = discountPercent;
    }

    public Bundle add(CatalogItem item) {
        children.add(item);
        return this;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int priceInr() {
        int sum = children.stream().mapToInt(CatalogItem::priceInr).sum(); // (2)!
        return sum - sum * discountPercent / 100;
    }

    @Override
    public String describe(String indent) {
        StringBuilder sb = new StringBuilder("%s+ %s (%d%% off): INR %,d".formatted(indent, name, discountPercent, priceInr()));
        children.forEach(c -> sb.append('\n').append(c.describe(indent + "    "))); // (3)!
        return sb.toString();
    }
}
