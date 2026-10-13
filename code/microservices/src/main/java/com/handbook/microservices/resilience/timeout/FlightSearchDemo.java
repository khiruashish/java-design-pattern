package com.handbook.microservices.resilience.timeout;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class FlightSearchDemo {

    public static void main(String[] args) {
        CountDownLatch never = new CountDownLatch(1);
        Map<String, Supplier<String>> suppliers = new LinkedHashMap<>();
        suppliers.put("SkyHop", () -> "SkyHop BLR->DEL ₹4,820");
        suppliers.put("AirNova", () -> {
            await(never); // supplier hangs
            return "AirNova BLR->DEL ₹4,650";
        });
        suppliers.put("CloudJet", () -> "CloudJet BLR->DEL ₹5,110");
        suppliers.put("BlueWing", () -> {
            throw new IllegalStateException("502 from BlueWing");
        });

        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            System.out.println("=== Travel: flight search with a 300 ms deadline per supplier ===");
            long start = System.nanoTime();
            var result = new FlightSearch(pool, 300).search(suppliers);
            long tookMs = (System.nanoTime() - start) / 1_000_000;
            System.out.println("Fares shown : " + result.fares());
            System.out.println("Timed out   : " + result.timedOut());
            System.out.println("Failed      : " + result.failed());
            System.out.println("Page returned within ~300 ms (not waiting for AirNova): " + (tookMs < 1_000));
            never.countDown();
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
