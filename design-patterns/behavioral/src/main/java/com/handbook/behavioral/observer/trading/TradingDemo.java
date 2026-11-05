package com.handbook.behavioral.observer.trading;

import java.util.Map;

public class TradingDemo {

    public static void main(String[] args) {
        System.out.println("=== Trading App: Price Alerts and Live Portfolio ===");
        PriceFeed feed = new PriceFeed();
        feed.subscribe(new PortfolioTracker(Map.of("ACME", 10, "GLOBEX", 5)));
        new PriceAlert("asha", "ACME", 1_900).attachTo(feed);

        feed.tick("ACME", 1_875.50);
        feed.tick("GLOBEX", 4_120.00);
        feed.tick("ACME", 1_902.25);
        feed.tick("ACME", 1_910.00); // alert already fired and unsubscribed
    }
}
