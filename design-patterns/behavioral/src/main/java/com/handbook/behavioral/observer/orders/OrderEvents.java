package com.handbook.behavioral.observer.orders;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** In-process event publisher with failure isolation between listeners. */
public class OrderEvents {

    public record OrderPlaced(String orderId, String email, List<String> skus, int amountInr) {
    }

    private final List<Consumer<OrderPlaced>> listeners = new ArrayList<>();

    public void onOrderPlaced(Consumer<OrderPlaced> listener) {
        listeners.add(listener);
    }

    public void publish(OrderPlaced event) {
        for (Consumer<OrderPlaced> l : listeners) {
            try {
                l.accept(event);
            } catch (RuntimeException e) { // (1)!
                System.out.println("  [events] listener failed: " + e.getMessage() + " (others continue)");
            }
        }
    }
}
