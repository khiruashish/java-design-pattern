package com.handbook.concurrency.rwlock.location;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class LocationDemo {

    public static void main(String[] args) {
        System.out.println("=== Ride-hailing: Optimistic Reads of a Driver's Position ===");
        DriverLocation driver = new DriverLocation();
        AtomicInteger torn = new AtomicInteger();
        AtomicInteger reads = new AtomicInteger();

        try (ExecutorService ex = Executors.newFixedThreadPool(5)) {
            ex.submit(() -> { // GPS updates: lat and lng always move together (lng = lat + 64.69)
                for (int i = 1; i <= 5_000; i++) {
                    double lat = 12.93 + i * 0.00001;
                    driver.move(lat, lat + 64.69);
                }
            });
            for (int r = 0; r < 4; r++) { // ETA and matching services
                ex.submit(() -> {
                    for (int i = 0; i < 50_000; i++) {
                        double[] p = driver.position();
                        if (p[1] - p[0] < 64.68 || p[1] - p[0] > 64.70) {
                            torn.incrementAndGet();
                        }
                        reads.incrementAndGet();
                    }
                });
            }
        }
        System.out.println("Position reads       : " + reads.get());
        System.out.println("Torn (mixed) reads   : " + torn.get());
        System.out.println("Most reads lock-free : " + (driver.fallbacks() < reads.get()));
    }
}
