package com.handbook.creational.singleton.grocery;

public class GroceryDemo {

    public static void main(String[] args) {
        System.out.println("=== Grocery Checkout Demo ===");

        CheckoutService checkout = new CheckoutService(); // first use triggers loading
        DeliverySlotService slots = new DeliverySlotService();

        System.out.println(checkout.checkout(1, 350));
        System.out.println(checkout.checkout(2, 620));
        System.out.println("Express delivery available: " + slots.expressAvailable());
        System.out.println("Same config instance in both services? "
                + (checkout.config() == slots.config()));
    }
}
