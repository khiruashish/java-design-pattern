package com.handbook.concurrency.dcl.lazy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class LazyDemo {

    public static void main(String[] args) {
        AtomicInteger loads = new AtomicInteger();
        Lazy<String> holidayCalendar = Lazy.of(() -> {
            loads.incrementAndGet();
            return "Bank holiday calendar 2027 (loaded from the core banking system)";
        });
        System.out.println("Initialized before first use? " + holidayCalendar.isInitialized());
        try (ExecutorService ex = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 200; i++) {
                ex.submit(holidayCalendar::get);
            }
        }
        System.out.println("Value : " + holidayCalendar.get());
        System.out.println("Loads : " + loads.get());
    }
}
