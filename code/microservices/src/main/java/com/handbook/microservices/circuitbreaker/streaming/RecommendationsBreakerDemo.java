package com.handbook.microservices.circuitbreaker.streaming;

import com.handbook.microservices.circuitbreaker.core.CircuitBreaker;
import com.handbook.microservices.circuitbreaker.core.CircuitBreaker.Config;
import com.handbook.microservices.support.FakeClock;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class RecommendationsBreakerDemo {

    public static void main(String[] args) {
        FakeClock clock = FakeClock.at("2026-10-13T21:00:00Z");
        AtomicBoolean overloaded = new AtomicBoolean(false);
        var breaker = new CircuitBreaker("recommender", new Config(4, 2, 50, Duration.ofSeconds(60), 1), clock);
        var client = new RecommendationsClient(breaker, profile -> {
            if (overloaded.get()) {
                throw new IllegalStateException("recommender overloaded");
            }
            return profile.equals("P-1") ? List.of("Monsoon Express", "Spice Route") : List.of("Byte Club");
        }, List.of("City of Rain", "Monsoon Express"));

        System.out.println("=== Streaming: personalised row with layered fallbacks ===");
        System.out.println("P-1: " + client.row("P-1"));
        System.out.println("-- Friday 9 pm peak: recommender overloaded --");
        overloaded.set(true);
        System.out.println("P-1: " + client.row("P-1"));
        System.out.println("P-2: " + client.row("P-2"));
        System.out.println("P-1: " + client.row("P-1") + "   <- breaker open, recommender not called");
        System.out.println("P-3: " + client.row("P-3"));
    }
}
