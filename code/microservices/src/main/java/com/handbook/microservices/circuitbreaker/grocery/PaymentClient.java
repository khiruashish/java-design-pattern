package com.handbook.microservices.circuitbreaker.grocery;

import com.handbook.microservices.circuitbreaker.core.CircuitBreaker;

import java.util.function.Supplier;

/** Checkout calls the payment provider through a breaker; when it's open, orders are queued, not lost. */
public final class PaymentClient {

    private final CircuitBreaker breaker;
    private final Supplier<String> provider;

    public PaymentClient(CircuitBreaker breaker, Supplier<String> provider) {
        this.breaker = breaker;
        this.provider = provider;
    }

    public String pay(String orderId) {
        return breaker.call(
                () -> orderId + " paid (" + provider.get() + ")",
                e -> orderId + " queued for retry: " + e.getMessage()); // (1)!
    }
}
