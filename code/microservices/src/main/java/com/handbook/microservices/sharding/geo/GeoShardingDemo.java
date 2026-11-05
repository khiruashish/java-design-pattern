package com.handbook.microservices.sharding.geo;

import java.util.Map;
import java.util.TreeMap;

public class GeoShardingDemo {

    public static void main(String[] args) {
        System.out.println("=== Ride-hailing: City-Based Shards and Splitting a Hot City ===");
        GeoShardDirectory dir = new GeoShardDirectory();
        dir.assign("bengaluru", "shard-south-1");
        dir.assign("chennai", "shard-south-2");
        dir.assign("mysuru", "shard-south-2");
        dir.assign("delhi", "shard-north-1");

        System.out.println("Bengaluru zone 12 -> " + dir.route("bengaluru", 12) + ", zone 140 -> " + dir.route("bengaluru", 140));
        Map<String, Integer> load = new TreeMap<>();
        for (int zone = 0; zone < 200; zone++) {
            load.merge(dir.route("bengaluru", zone), 1, Integer::sum);
        }
        System.out.println("Before split, Bengaluru's 200 zones: " + load);

        dir.split("bengaluru", 100, "shard-south-3"); // Friday-evening surge: move zones 100+ to a new shard
        load.clear();
        for (int zone = 0; zone < 200; zone++) {
            load.merge(dir.route("bengaluru", zone), 1, Integer::sum);
        }
        System.out.println("After split,  Bengaluru's 200 zones: " + load);
        System.out.println("Chennai and Delhi routing unchanged: " + dir.route("chennai", 5) + ", " + dir.route("delhi", 5));
    }
}
