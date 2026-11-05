package com.handbook.concurrency.future.productpage;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Product page: price, stock and reviews fetched in parallel and combined; reviews may fail. */
public class ProductPageDemo {

    record Page(String sku, int priceInr, int stock, List<String> reviews) {
    }

    static int price(String sku) { sleep(50); return 1_299; }

    static int stock(String sku) { sleep(80); return 14; }

    static List<String> reviews(String sku) {
        sleep(30);
        throw new IllegalStateException("reviews service returned 503");
    }

    static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        System.out.println("=== E-commerce: Product Page Composition ===");
        String sku = "HEADPHONES-42";
        try (ExecutorService io = Executors.newVirtualThreadPerTaskExecutor()) {
            CompletableFuture<Integer> price = CompletableFuture.supplyAsync(() -> price(sku), io);
            CompletableFuture<Integer> stock = CompletableFuture.supplyAsync(() -> stock(sku), io);
            CompletableFuture<List<String>> reviews = CompletableFuture.supplyAsync(() -> reviews(sku), io)
                    .exceptionally(e -> { // (1)!
                        System.out.println("  reviews unavailable (" + e.getCause().getMessage() + "), showing page without them");
                        return List.of();
                    });

            Page page = price.thenCombine(stock, (p, s) -> new Page(sku, p, s, List.of())) // (2)!
                    .thenCombine(reviews, (pg, r) -> new Page(pg.sku(), pg.priceInr(), pg.stock(), r))
                    .join();
            System.out.println("Rendered: " + page);
            System.out.println("Price and stock were essential; reviews were optional.");
        }
    }
}
