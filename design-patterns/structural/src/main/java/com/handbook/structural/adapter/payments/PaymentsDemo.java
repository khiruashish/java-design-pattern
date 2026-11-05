package com.handbook.structural.adapter.payments;

public class PaymentsDemo {

    public static void main(String[] args) {
        System.out.println("=== Payments: Checkout on a Legacy Bank SDK ===");
        CheckoutService checkout = new CheckoutService(new LegacyBankAdapter(new LegacyBankGateway()));
        System.out.println(checkout.pay("ORD-1001", "499.00"));
        System.out.println(checkout.pay("ORD-1002", "12999.50"));
        System.out.println(checkout.pay("ORD-1003", "75000.00"));
    }
}
