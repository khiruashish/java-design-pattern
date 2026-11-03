package com.handbook.enterprise.repository.grocery;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Adapter #1: for unit tests and local demos. */
public final class InMemoryProductRepository implements ProductRepository {

    private final Map<String, Product> rows = new ConcurrentHashMap<>();

    public void save(Product product) { rows.put(product.sku(), product); }

    public Optional<Product> findBySku(String sku) { return Optional.ofNullable(rows.get(sku)); }

    public List<Product> lowStock(int threshold) {
        return rows.values().stream().filter(p -> p.stock() < threshold)
                .sorted(Comparator.comparing(Product::sku)).toList();
    }
}
