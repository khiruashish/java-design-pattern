package com.handbook.enterprise.di.grocery;

import com.handbook.enterprise.di.grocery.Checkout.RecordingNotifier;

public class GroceryDiDemo {

    public static void main(String[] args) {
        System.out.println("=== Grocery: Same CheckoutService, Two Wirings ===");
        System.out.println("Production wiring:");
        System.out.println("  " + CompositionRoot.production().placeOrder("ORD-101", "+91-98450", "560034", 45_900));

        RecordingNotifier recorder = new RecordingNotifier();
        System.out.println("Demo wiring (fake payments, recorded messages):");
        System.out.println("  " + CompositionRoot.demo(recorder).placeOrder("ORD-102", "+91-99001", "110001", 12_000));
        System.out.println("  recorded: " + recorder.sent);
        System.out.println("CheckoutService.java imports no concrete class: swapping needed zero changes to it.");
    }
}
