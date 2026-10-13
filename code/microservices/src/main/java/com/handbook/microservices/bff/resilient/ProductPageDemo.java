package com.handbook.microservices.bff.resilient;

import com.handbook.microservices.support.Json;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public class ProductPageDemo {

    public static void main(String[] args) throws Exception {
        var neverReleased = new CountDownLatch(1);
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            System.out.println("=== Challenge: parallel fan-out with graceful degradation ===");

            var healthy = new ProductPageBff(pool, () -> "Alphonso mangoes 1 kg", () -> "₹399",
                    () -> List.of("Sweet!", "Fresh"), 200);
            System.out.println("All services up      -> " + Json.of(healthy.page()));

            var slowReviews = new ProductPageBff(pool, () -> "Alphonso mangoes 1 kg", () -> "₹399", () -> {
                await(neverReleased); // reviews service hangs
                return List.of();
            }, 200);
            System.out.println("Reviews hang         -> " + Json.of(slowReviews.page()));

            var priceDown = new ProductPageBff(pool, () -> "Alphonso mangoes 1 kg",
                    () -> { throw new IllegalStateException("pricing 503"); }, () -> List.of("Sweet!"), 200);
            System.out.println("Pricing returns 503  -> " + Json.of(priceDown.page()));

            var productDown = new ProductPageBff(pool, () -> { throw new IllegalStateException("503"); },
                    () -> "₹399", () -> List.of(), 200);
            System.out.println("Catalog returns 503  -> " + Json.of(productDown.page()));
            neverReleased.countDown();
        }
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
