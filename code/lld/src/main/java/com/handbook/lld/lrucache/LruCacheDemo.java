package com.handbook.lld.lrucache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class LruCacheDemo {

    public static void main(String[] args) throws InterruptedException {
        var cache = new LruCache<String, String>(3);

        System.out.println("=== LLD: LRU cache (capacity 3) for product details ===");
        cache.put("MANGO", "Alphonso ₹399");
        cache.put("RICE", "Basmati ₹649");
        cache.put("MILK", "Toned ₹56");
        System.out.println("after 3 puts      : " + cache);
        System.out.println("get MANGO         : " + cache.get("MANGO"));
        System.out.println("order now         : " + cache);
        cache.put("BREAD", "Whole wheat ₹45");
        System.out.println("put BREAD (evicts least recently used RICE): " + cache);
        System.out.println("get RICE          : " + cache.get("RICE"));

        var shared = new LruCache<Integer, Integer>(100);
        CountDownLatch start = new CountDownLatch(1);
        List<Thread> threads = new ArrayList<>();
        for (int t = 0; t < 8; t++) {
            int offset = t * 1_000;
            threads.add(Thread.ofPlatform().start(() -> {
                try {
                    start.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                for (int i = 0; i < 1_000; i++) {
                    shared.put(offset + i, i);
                    shared.get(offset + i / 2);
                }
            }));
        }
        start.countDown();
        for (Thread t : threads) {
            t.join();
        }
        System.out.println("8 threads x 1,000 puts into capacity 100 -> evictions: " + shared.evictions()
                + " (exactly 8,000 - 100)");
    }
}
