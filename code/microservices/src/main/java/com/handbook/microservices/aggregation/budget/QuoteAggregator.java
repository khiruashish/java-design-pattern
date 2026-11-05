package com.handbook.microservices.aggregation.budget;

import com.handbook.microservices.aggregation.core.SimCall;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Fan out to every insurer, keep whatever answers within the latency budget, and say what's missing. */
public final class QuoteAggregator {

    public record Quote(String insurer, long premiumPaise) { }

    public record Page(List<Quote> quotes, List<String> missing, long respondedAtMs) { }

    private final long budgetMs;

    public QuoteAggregator(long budgetMs) {
        this.budgetMs = budgetMs;
    }

    public Page aggregate(List<SimCall> insurers) {
        List<Quote> quotes = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        long latest = 0;
        for (SimCall call : insurers) {
            if (call.fails()) {
                missing.add(call.backend() + " (error)"); // (1)!
            } else if (call.latencyMs() > budgetMs) {
                missing.add(call.backend() + " (timed out at " + budgetMs + " ms)"); // (2)!
            } else {
                quotes.add(new Quote(call.backend(), (Long) call.result().get("premium")));
                latest = Math.max(latest, call.latencyMs());
            }
        }
        boolean anyMissing = !missing.isEmpty();
        quotes.sort(Comparator.comparingLong(Quote::premiumPaise));
        return new Page(quotes, missing, anyMissing ? budgetMs : latest); // (3)!
    }
}
