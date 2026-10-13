package com.handbook.lld.systemdesign;

public class ConsistentHashingDemo {

    public static void main(String[] args) {
        int keys = 100_000;
        var ring = new ConsistentHashRing(200);
        for (String n : new String[] {"cache-1", "cache-2", "cache-3", "cache-4"}) {
            ring.addNode(n);
        }
        String[] before = new String[keys];
        for (int i = 0; i < keys; i++) {
            before[i] = ring.nodeFor("cart:" + i);
        }

        System.out.println("=== System design: consistent hashing for a cart cache cluster ===");
        System.out.println("4 nodes, 200 virtual nodes each: " + ConsistentHashRing.distribution(ring, keys));

        ring.addNode("cache-5");
        int moved = 0;
        for (int i = 0; i < keys; i++) {
            if (!ring.nodeFor("cart:" + i).equals(before[i])) {
                moved++;
            }
        }
        System.out.printf("add cache-5 -> %.1f%% of keys moved (ideal 1/5 = 20%%)%n", moved * 100.0 / keys);

        int movedModulo = 0;
        for (int i = 0; i < keys; i++) {
            int h = Math.floorMod(("cart:" + i).hashCode(), 4);
            int h5 = Math.floorMod(("cart:" + i).hashCode(), 5);
            if (h != h5) {
                movedModulo++;
            }
        }
        System.out.printf("with hash %% N instead: %.1f%% of keys would move%n", movedModulo * 100.0 / keys);
    }
}
