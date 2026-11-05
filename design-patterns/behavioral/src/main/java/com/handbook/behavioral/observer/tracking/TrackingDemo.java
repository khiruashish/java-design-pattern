package com.handbook.behavioral.observer.tracking;

public class TrackingDemo {

    public static void main(String[] args) {
        System.out.println("=== Grocery Delivery: Live Tracking ===");
        DeliveryTracker tracker = new DeliveryTracker("GRO-981");

        tracker.attach(new DeliveryTracker.Observer() { // customer app
            public void locationChanged(String id, double km) {
                System.out.printf("  [app] rider is %.1f km away%n", km);
            }
            public void delivered(String id) { System.out.println("  [app] Delivered! Rate your order"); }
        });
        tracker.attach(new DeliveryTracker.Observer() { // ETA service, notifies only on meaningful change
            private int lastEta = -1;
            public void locationChanged(String id, double km) {
                int eta = (int) Math.ceil(km / 0.4); // 24 km/h in city traffic
                if (eta != lastEta) {
                    System.out.println("  [eta] " + id + " ETA " + eta + " min");
                    lastEta = eta;
                }
            }
            public void delivered(String id) { System.out.println("  [eta] closed tracking for " + id); }
        });

        for (double km : new double[] {2.4, 1.9, 1.8, 0.3}) {
            tracker.ping(km);
        }
        tracker.markDelivered();
        System.out.println("observers left: " + tracker.observerCount());
    }
}
