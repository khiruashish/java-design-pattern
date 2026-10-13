package com.handbook.microservices.circuitbreaker.grocery;

import com.handbook.microservices.circuitbreaker.core.CircuitBreaker;
import com.handbook.microservices.circuitbreaker.core.CircuitBreaker.Config;
import com.handbook.microservices.support.FakeClock;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PaymentBreakerDemo {

    public static void main(String[] args) {
        FakeClock clock = FakeClock.at("2026-10-13T18:00:00Z");
        AtomicBoolean providerDown = new AtomicBoolean(false);
        AtomicInteger providerCalls = new AtomicInteger();

        var breaker = new CircuitBreaker("payment", new Config(4, 4, 50, Duration.ofSeconds(30), 2), clock);
        var client = new PaymentClient(breaker, () -> {
            providerCalls.incrementAndGet();
            if (providerDown.get()) {
                throw new IllegalStateException("provider timeout");
            }
            return "txn-ok";
        });

        System.out.println("=== Grocery: checkout -> payment provider ===");
        pay(client, "ORD-1");
        pay(client, "ORD-2");
        System.out.println("-- provider starts timing out --");
        providerDown.set(true);
        pay(client, "ORD-3");
        pay(client, "ORD-4");
        int before = providerCalls.get();
        pay(client, "ORD-5");
        pay(client, "ORD-6");
        System.out.println("Provider calls while OPEN: " + (providerCalls.get() - before) + " (fail fast, no waiting)");

        System.out.println("-- 30 s later, provider has recovered --");
        clock.advance(Duration.ofSeconds(30));
        providerDown.set(false);
        pay(client, "ORD-7");
        pay(client, "ORD-8");
        pay(client, "ORD-9");
    }

    private static void pay(PaymentClient client, String orderId) {
        System.out.println(client.pay(orderId));
    }
}
