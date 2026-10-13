package com.handbook.behavioral.strategy.delivery;

import com.handbook.behavioral.strategy.delivery.Checkout.Customer;

public class DeliveryDemo {

    public static void main(String[] args) {
        System.out.println("=== Grocery: Delivery Fee Strategies ===");
        Checkout checkout = new Checkout();
        System.out.println(checkout.total(new Customer("Asha", false, false), 150, 2.0, 18));
        System.out.println(checkout.total(new Customer("Asha", false, false), 450, 2.0, 18));
        System.out.println(checkout.total(new Customer("Ravi", false, true), 450, 4.5, 18));
        System.out.println(checkout.total(new Customer("Meera", true, true), 90, 6.0, 18));
        System.out.println(checkout.total(new Customer("Kabir", false, false), 150, 1.0, 23));
    }
}
