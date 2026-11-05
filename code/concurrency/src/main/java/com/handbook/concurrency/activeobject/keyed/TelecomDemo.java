package com.handbook.concurrency.activeobject.keyed;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

public class TelecomDemo {

    public static void main(String[] args) {
        System.out.println("=== Telecom: Per-Subscriber Ordering for Recharge Events ===");
        Map<String, List<String>> applied = new ConcurrentHashMap<>();
        try (var pool = Executors.newFixedThreadPool(8)) {
            KeyedSerialExecutor executor = new KeyedSerialExecutor(pool);
            List<CompletableFuture<?>> all = new ArrayList<>();
            String[] subscribers = {"+919845000001", "+919845000002", "+919845000003"};
            String[] steps = {"recharge INR 299", "activate 1.5GB/day", "send SMS"};
            for (String step : steps) {
                for (String msisdn : subscribers) {
                    all.add(executor.submit(msisdn, () -> {
                        applied.computeIfAbsent(msisdn, k -> new ArrayList<>()).add(step);
                        return null;
                    }));
                }
            }
            CompletableFuture.allOf(all.toArray(CompletableFuture[]::new)).join();
        }
        new TreeMap<>(applied).forEach((msisdn, log) -> System.out.println(msisdn + " -> " + log));
        System.out.println("Each subscriber's steps stayed in order while subscribers were processed in parallel.");
    }
}
