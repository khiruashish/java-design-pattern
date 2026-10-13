package com.handbook.capstone.catalog;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** The catalog module's public API (a port), with the real store and a caching proxy. */
public interface Catalog {

    Optional<Product> find(String sku);

    /** The "database" (slow in real life). */
    final class InMemoryCatalog implements Catalog {
        private final Map<String, Product> products = new ConcurrentHashMap<>();
        int reads;

        public InMemoryCatalog add(Product p) {
            products.put(p.sku(), p);
            return this;
        }

        @Override
        public Optional<Product> find(String sku) {
            reads++;
            return Optional.ofNullable(products.get(sku));
        }

        public int reads() {
            return reads;
        }
    }

    /** [Proxy] Same interface, adds caching; callers can't tell the difference. */
    final class CachingCatalog implements Catalog {
        private final Catalog target;
        private final Map<String, Optional<Product>> cache = new ConcurrentHashMap<>();

        public CachingCatalog(Catalog target) {
            this.target = target;
        }

        @Override
        public Optional<Product> find(String sku) {
            return cache.computeIfAbsent(sku, target::find);
        }
    }
}
