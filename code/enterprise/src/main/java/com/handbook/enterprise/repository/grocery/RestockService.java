package com.handbook.enterprise.repository.grocery;

import java.util.List;

/** Application logic written once against the port; works with any adapter. */
public final class RestockService {

    private final ProductRepository products;

    public RestockService(ProductRepository products) {
        this.products = products;
    }

    public List<String> restockLowItems(int threshold, int topUpTo) {
        return products.lowStock(threshold).stream().map(p -> {
            products.save(p.withStock(topUpTo));
            return p.sku() + " " + p.stock() + "->" + topUpTo;
        }).toList();
    }
}
