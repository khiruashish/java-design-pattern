package com.handbook.structural.facade.checkout;

import com.handbook.structural.facade.checkout.Subsystems.*;

public class CheckoutDemo {

    public static void main(String[] args) {
        System.out.println("=== Grocery: One-Tap Checkout ===");
        CheckoutFacade checkout = new CheckoutFacade(new InventoryService(), new PaymentService(),
                new DeliveryService(), new NotificationService());

        System.out.println("Order A:");
        System.out.println("=> " + checkout.placeOrder("CUST-1", "Koramangala", "MILK-1L", 2, 136));
        System.out.println("Order B:");
        System.out.println("=> " + checkout.placeOrder("CUST-2", "Indiranagar", "EGGS-12", 1, 90));
        System.out.println("Order C:");
        System.out.println("=> " + checkout.placeOrder("CUST-3", "HSR Layout", "BREAD-WW", 3, 9_000));
    }
}
