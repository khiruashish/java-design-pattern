package com.handbook.microservices.circuitbreaker.mini;

import java.util.concurrent.atomic.AtomicLong;

public class MiniBreakerDemo {

    public static void main(String[] args) {
        AtomicLong now = new AtomicLong(0);
        var breaker = new MiniCircuitBreaker(3, 5_000, now::get);
        String[] results = {"ok", "fail", "fail", "fail", "ok", "ok"};

        System.out.println("=== Challenge: minimal circuit breaker (3 consecutive failures, 5 s open) ===");
        for (String r : results) {
            String out = breaker.call(() -> {
                if (r.equals("fail")) {
                    throw new IllegalStateException();
                }
                return "real";
            }, () -> "fallback");
            System.out.printf("t=%4dms service=%-4s -> %-8s state=%s%n", now.get(), r, out, breaker.state());
            now.addAndGet(1_000);
        }
        now.set(9_000);
        String out = breaker.call(() -> "real", () -> "fallback");
        System.out.printf("t=%4dms service=ok   -> %-8s state=%s%n", now.get(), out, breaker.state());
    }
}
