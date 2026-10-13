package com.handbook.creational.singleton.payments;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PaymentsDemo {

    public static void main(String[] args) {
        System.out.println("=== Payments: Transaction ID Generator ===");
        TransactionIdGenerator generator = TransactionIdGenerator.getInstance();
        System.out.println("First ID  : " + generator.nextId());
        System.out.println("Second ID : " + generator.nextId());

        int threads = 1_000;
        int idsPerThread = 10;
        Set<String> ids = ConcurrentHashMap.newKeySet();

        System.out.println("Generating 10,000 IDs from 1,000 virtual threads...");
        try (ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int t = 0; t < threads; t++) {
                pool.submit(() -> {
                    for (int i = 0; i < idsPerThread; i++) {
                        // every thread asks for "the" generator; all get the same object
                        ids.add(TransactionIdGenerator.getInstance().nextId());
                    }
                });
            }
        } // close() waits for all tasks to finish

        System.out.println("Unique IDs : " + ids.size());
        System.out.println("Duplicates : " + (threads * idsPerThread - ids.size()));
        System.out.println("Next ID    : " + generator.nextId());
    }
}
