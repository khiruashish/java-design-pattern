package com.handbook.microservices.backpressure.overflow;

import com.handbook.microservices.backpressure.overflow.IntakeBuffer.Policy;

public class OverflowDemo {

    public static void main(String[] args) {
        System.out.println("=== Grocery: Picking API Is Slow; 7 Requests Arrive for a Buffer of 4 ===");
        for (Policy policy : Policy.values()) {
            IntakeBuffer buffer = new IntakeBuffer(4, policy);
            StringBuilder outcomes = new StringBuilder();
            for (int i = 1; i <= 7; i++) {
                String result = buffer.offer("pick-" + i);
                if (i > 4) {
                    outcomes.append("\n    pick-").append(i).append(": ").append(result);
                }
            }
            System.out.println(policy + " -> buffer " + buffer.contents() + outcomes);
        }
        System.out.println("REJECT_NEW for orders (client retries), DROP_OLDEST for live location, CALLER_WAITS inside one process.");
    }
}
