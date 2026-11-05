package com.handbook.behavioral.observer.trading;

import java.util.HashMap;
import java.util.Map;

/** Observer: keeps a running portfolio value. */
public class PortfolioTracker implements PriceFeed.PriceListener {

    private final Map<String, Integer> holdings;
    private final Map<String, Double> lastPrice = new HashMap<>();

    public PortfolioTracker(Map<String, Integer> holdings) {
        this.holdings = holdings;
    }

    @Override
    public void onPrice(String symbol, double price) {
        if (holdings.containsKey(symbol)) {
            lastPrice.put(symbol, price);
            double value = holdings.entrySet().stream()
                    .mapToDouble(e -> e.getValue() * lastPrice.getOrDefault(e.getKey(), 0.0)).sum();
            System.out.printf("  [portfolio] value INR %,.2f%n", value);
        }
    }
}
