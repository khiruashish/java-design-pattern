package com.handbook.microservices.circuitbreaker.streaming;

import com.handbook.microservices.circuitbreaker.core.CircuitBreaker;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Personalised rows are nice to have. If the recommender is struggling, fall back to the profile's
 * last good response, then to a global "Popular now" row. The home screen never fails.
 */
public final class RecommendationsClient {

    private final CircuitBreaker breaker;
    private final Function<String, List<String>> recommender;
    private final Map<String, List<String>> lastGood = new ConcurrentHashMap<>();
    private final List<String> popularNow;

    public RecommendationsClient(CircuitBreaker breaker, Function<String, List<String>> recommender,
                                 List<String> popularNow) {
        this.breaker = breaker;
        this.recommender = recommender;
        this.popularNow = popularNow;
    }

    public String row(String profileId) {
        return breaker.call(() -> {
            List<String> titles = recommender.apply(profileId);
            lastGood.put(profileId, titles); // (1)!
            return "For you " + titles;
        }, e -> {
            List<String> cached = lastGood.get(profileId);
            return cached != null ? "For you (cached) " + cached : "Popular now " + popularNow; // (2)!
        });
    }
}
