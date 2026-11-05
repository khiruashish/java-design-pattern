package com.handbook.behavioral.observer.trading;

import java.util.ArrayList;
import java.util.List;

/** Subject: publishes price ticks to whoever subscribed. */
public class PriceFeed {

    @FunctionalInterface
    public interface PriceListener { // (1)!
        void onPrice(String symbol, double price);
    }

    private final List<PriceListener> listeners = new ArrayList<>();

    public Runnable subscribe(PriceListener listener) {
        listeners.add(listener);
        return () -> listeners.remove(listener); // (2)!
    }

    public void tick(String symbol, double price) {
        System.out.printf("TICK %s %.2f%n", symbol, price);
        List.copyOf(listeners).forEach(l -> l.onPrice(symbol, price)); // (3)!
    }
}
