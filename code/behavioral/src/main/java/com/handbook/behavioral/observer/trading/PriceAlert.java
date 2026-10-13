package com.handbook.behavioral.observer.trading;

/** Observer: fires once when a symbol crosses a target, then unsubscribes itself. */
public class PriceAlert implements PriceFeed.PriceListener {

    private final String user;
    private final String symbol;
    private final double target;
    private Runnable unsubscribe;

    public PriceAlert(String user, String symbol, double target) {
        this.user = user;
        this.symbol = symbol;
        this.target = target;
    }

    public void attachTo(PriceFeed feed) {
        unsubscribe = feed.subscribe(this);
    }

    @Override
    public void onPrice(String s, double price) {
        if (s.equals(symbol) && price >= target) {
            System.out.printf("  [alert] %s: %s crossed %.2f (now %.2f)%n", user, symbol, target, price);
            unsubscribe.run(); // one-shot alert
        }
    }
}
