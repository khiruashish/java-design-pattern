package com.handbook.principles.other.yagni;

import java.util.HashMap;
import java.util.Map;

public class YagniDemo {
    public static void main(String[] args) {
        Map<String, Double> cacheStore = new HashMap<>();
        var heavy = new YagniExamples.OverEngineeredConverter(
                Map.of("fixed", (from, to) -> 0.012),
                new YagniExamples.RateCache() {
                    public Double lookup(String pair) { return cacheStore.get(pair); }
                    public void store(String pair, double rate) { cacheStore.put(pair, rate); }
                },
                (from, to, in, out) -> { });
        System.out.println("over-engineered: 10000 paise -> " + heavy.convert("fixed", "INR", "USD", 10_000)
                + " USD cents (3 interfaces + 1 class to wire)");
        System.out.println("just enough    : 10000 paise -> " + new YagniExamples.InrToUsd(120).convertPaise(10_000)
                + " USD cents (1 record)");
    }
}
