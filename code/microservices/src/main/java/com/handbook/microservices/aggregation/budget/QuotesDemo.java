package com.handbook.microservices.aggregation.budget;

import com.handbook.microservices.aggregation.budget.QuoteAggregator.Page;
import com.handbook.microservices.aggregation.core.SimCall;

import java.util.List;
import java.util.Map;

public class QuotesDemo {

    public static void main(String[] args) {
        System.out.println("=== Insurance: Car Insurance Quotes from 5 Insurers within 800 ms ===");
        List<SimCall> insurers = List.of(
                SimCall.ok("Suraksha General", 220, Map.of("premium", 1_245_000L)),
                SimCall.ok("Bharat Shield", 640, Map.of("premium", 1_189_900L)),
                SimCall.ok("Kavach Insure", 1_900, Map.of("premium", 1_099_000L)), // slow partner API
                SimCall.failing("Monsoon Mutual", 150),
                SimCall.ok("Raksha Direct", 410, Map.of("premium", 1_310_500L)));
        Page page = new QuoteAggregator(800).aggregate(insurers);
        page.quotes().forEach(q -> System.out.printf("  %-17s INR %,d%n", q.insurer(), q.premiumPaise() / 100));
        System.out.println("Missing: " + page.missing());
        System.out.println("Page returned at " + page.respondedAtMs() + " ms with partial results, instead of waiting 1,900 ms or failing.");
    }
}
