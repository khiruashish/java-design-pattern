package com.handbook.concurrency.guarded.delivery;

public class DeliveryDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Food Delivery: Riders Wait at the Counter Until the Order Is Packed ===");
        PickupCounter counter = new PickupCounter();
        String[] result = new String[2];

        Thread rider1 = Thread.ofVirtual().start(() -> {
            try {
                result[0] = counter.awaitPickup("ORD-501", 2_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        Thread rider2 = Thread.ofVirtual().start(() -> {
            try {
                result[1] = counter.awaitPickup("ORD-999", 200); // kitchen never finishes this one in time
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread.sleep(100); // kitchen is still cooking; riders are suspended, not spinning
        counter.markPacked("ORD-501", "shelf B3");
        rider1.join();
        rider2.join();
        System.out.println("Rider for ORD-501 picked up from: " + result[0]);
        System.out.println("Rider for ORD-999 got: " + result[1] + " -> timed out, reassigned to another pickup");
    }
}
