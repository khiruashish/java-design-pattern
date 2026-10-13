package com.handbook.microservices.bff.resilient;

import com.handbook.microservices.support.Json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Coding challenge: fan out to three services in parallel, give each a deadline,
 * and degrade gracefully instead of failing the whole page.
 */
public final class ProductPageBff {

    private final ExecutorService pool;
    private final Supplier<Object> product;
    private final Supplier<Object> price;
    private final Supplier<Object> reviews;
    private final long timeoutMillis;

    public ProductPageBff(ExecutorService pool, Supplier<Object> product, Supplier<Object> price,
                          Supplier<Object> reviews, long timeoutMillis) {
        this.pool = pool;
        this.product = product;
        this.price = price;
        this.reviews = reviews;
        this.timeoutMillis = timeoutMillis;
    }

    public Map<String, Object> page() {
        List<String> degraded = new ArrayList<>();
        var p = call("product", product, null, degraded);
        var pr = call("price", price, null, degraded);
        var r = call("reviews", reviews, List.of(), degraded); // optional section: empty fallback
        CompletableFuture.allOf(p, pr, r).join();
        if (p.join() == null) {
            return Json.obj("error", "PRODUCT_UNAVAILABLE"); // the page is useless without the product
        }
        return Json.obj("product", p.join(), "price", pr.join(), "reviews", r.join(),
                "degraded", List.copyOf(degraded));
    }

    private CompletableFuture<Object> call(String name, Supplier<Object> s, Object fallback, List<String> degraded) {
        return CompletableFuture.supplyAsync(s, pool)
                .orTimeout(timeoutMillis, TimeUnit.MILLISECONDS)
                .exceptionally(e -> {
                    synchronized (degraded) {
                        degraded.add(name);
                    }
                    return fallback;
                });
    }
}
