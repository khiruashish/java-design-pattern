package com.handbook.microservices.caching.writebehind;

import java.util.Random;
import java.util.TreeMap;

public class WriteBehindDemo {

    public static void main(String[] args) {
        System.out.println("=== Travel: Hotel Page-View Counters with Write-Behind ===");
        WriteBehindCounter counter = new WriteBehindCounter();
        Random random = new Random(7);
        String[] hotels = {"GOA-BEACH-12", "JAIPUR-FORT-3", "MUNNAR-TEA-8"};

        for (int second = 1; second <= 3; second++) {
            for (int i = 0; i < 20_000; i++) {
                counter.increment(hotels[random.nextInt(hotels.length)]);
            }
            System.out.println("t=" + second + "s flush wrote " + counter.flush() + " rows");
        }
        System.out.println("60,000 views -> " + counter.dbWrites() + " DB writes; totals " + new TreeMap<>(counter.database()));

        for (int i = 0; i < 500; i++) {
            counter.increment("GOA-BEACH-12");
        }
        System.out.println("Crash before the next flush would lose " + counter.pending() + " views: fine for counters, never for money.");
    }
}
