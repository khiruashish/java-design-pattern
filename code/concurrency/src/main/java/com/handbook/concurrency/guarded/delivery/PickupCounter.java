package com.handbook.concurrency.guarded.delivery;

import java.util.HashMap;
import java.util.Map;

/** Guarded Suspension: a rider waits until the guard "order is packed" becomes true. */
public final class PickupCounter {

    private final Map<String, String> packed = new HashMap<>(); // orderId -> shelf

    public synchronized void markPacked(String orderId, String shelf) {
        packed.put(orderId, shelf);
        notifyAll(); // (1)!
    }

    public synchronized String awaitPickup(String orderId, long timeoutMs) throws InterruptedException {
        long deadline = System.nanoTime() + timeoutMs * 1_000_000;
        while (!packed.containsKey(orderId)) { // (2)!
            long remainingMs = (deadline - System.nanoTime()) / 1_000_000;
            if (remainingMs <= 0) {
                return null; // (3)!
            }
            wait(remainingMs);
        }
        return packed.remove(orderId);
    }
}
