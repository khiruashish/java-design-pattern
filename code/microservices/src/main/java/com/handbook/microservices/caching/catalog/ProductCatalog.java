package com.handbook.microservices.caching.catalog;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/** Cache-aside: the APPLICATION reads the cache, falls back to the DB, and fills the cache itself. */
public final class ProductCatalog {

    public record Product(String sku, String name, long pricePaise) { }

    private final Map<String, Product> database = new HashMap<>();
    private final TtlCache<Product> cache;
    private int dbReads;

    public ProductCatalog(TtlCache<Product> cache) {
        this.cache = cache;
    }

    public void seed(Product p) {
        database.put(p.sku(), p);
    }

    public Product get(String sku) {
        return cache.get("product:" + sku).orElseGet(() -> { // (1)!
            dbReads++;
            Product fromDb = database.get(sku);
            cache.set("product:" + sku, fromDb, Duration.ofMinutes(10)); // (2)!
            return fromDb;
        });
    }

    public void updatePrice(String sku, long newPaise) {
        Product p = database.get(sku);
        database.put(sku, new Product(sku, p.name(), newPaise)); // 1. write the source of truth
        cache.delete("product:" + sku);                         // 2. invalidate (don't set) (3)!
    }

    public int dbReads() {
        return dbReads;
    }
}
