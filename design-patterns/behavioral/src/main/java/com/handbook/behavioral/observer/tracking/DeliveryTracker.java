package com.handbook.behavioral.observer.tracking;

import java.util.ArrayList;
import java.util.List;

/** Subject: one per delivery. Observers are the customer app and the ETA service. */
public class DeliveryTracker {

    public interface Observer {
        void locationChanged(String orderId, double kmAway);

        void delivered(String orderId);
    }

    private final String orderId;
    private final List<Observer> observers = new ArrayList<>();

    public DeliveryTracker(String orderId) {
        this.orderId = orderId;
    }

    public void attach(Observer o) { observers.add(o); }

    public void ping(double kmAway) {
        observers.forEach(o -> o.locationChanged(orderId, kmAway));
    }

    public void markDelivered() {
        observers.forEach(o -> o.delivered(orderId));
        observers.clear(); // (1)!
    }

    public int observerCount() { return observers.size(); }
}
