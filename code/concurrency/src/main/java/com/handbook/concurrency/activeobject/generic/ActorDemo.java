package com.handbook.concurrency.activeobject.generic;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public class ActorDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Challenge: A Generic Actor<S> Around a Plain HashMap ===");
        System.out.println("A plain HashMap hit by 8 threads loses updates (and the count changes every run).");
        try (Actor<Map<String, Integer>> counter = new Actor<>(new HashMap<>());
             var pool = Executors.newFixedThreadPool(8)) {
            for (int i = 0; i < 20_000; i++) {
                pool.submit(() -> counter.ask(m -> m.merge("views", 1, Integer::sum)));
            }
            pool.close();
            System.out.println("The same HashMap inside an Actor, 20,000 increments from 8 threads: views=" + counter.ask(m -> m.get("views")).get());
        }
    }
}
