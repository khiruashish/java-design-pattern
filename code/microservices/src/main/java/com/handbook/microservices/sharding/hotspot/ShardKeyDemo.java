package com.handbook.microservices.sharding.hotspot;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

/** Coding challenge: why "range by order date" is a terrible shard key for writes. */
public class ShardKeyDemo {

    static final int SHARDS = 4;

    static int byDateRange(LocalDate day) {
        int quarter = (day.getMonthValue() - 1) / 3;
        return quarter; // Q1 -> shard 0, Q2 -> shard 1, ...
    }

    static int byHashedOrderId(String orderId) {
        return Math.floorMod(orderId.hashCode() * 0x9E3779B1, SHARDS); // spread similar ids
    }

    public static void main(String[] args) {
        System.out.println("=== Challenge: Picking a Shard Key (today's 100,000 new orders) ===");
        LocalDate today = LocalDate.of(2026, 5, 14);
        Map<Integer, Integer> range = new TreeMap<>();
        Map<Integer, Integer> hashed = new TreeMap<>();
        for (int i = 0; i < SHARDS; i++) {
            range.put(i, 0);
            hashed.put(i, 0);
        }
        for (int i = 0; i < 100_000; i++) {
            range.merge(byDateRange(today), 1, Integer::sum);
            hashed.merge(byHashedOrderId("ORD-" + (20260514_000_000L + i)), 1, Integer::sum);
        }
        System.out.println("Range by date  : " + range + "  <- every write hits one hot shard");
        System.out.println("Hash of orderId: " + hashed + "  <- writes spread evenly");
        System.out.println("But 'orders between two dates' now needs scatter-gather: pick the key for your main access path.");
    }
}
