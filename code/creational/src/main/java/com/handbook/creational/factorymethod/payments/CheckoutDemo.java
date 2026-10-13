package com.handbook.creational.factorymethod.payments;

public class CheckoutDemo {

    public static void main(String[] args) {
        System.out.println("=== Payments: Checkout with Pluggable Processors ===");
        PaymentProcessorFactory factory = new PaymentProcessorFactory()
                .register(PaymentMethod.UPI, UpiProcessor::new)
                .register(PaymentMethod.CARD, CardProcessor::new)
                .register(PaymentMethod.WALLET, WalletProcessor::new);

        pay(factory, "ORD-101", PaymentMethod.UPI, 450);
        pay(factory, "ORD-102", PaymentMethod.CARD, 2400);
        pay(factory, "ORD-103", PaymentMethod.WALLET, 12000);
        pay(factory, "ORD-104", PaymentMethod.NET_BANKING, 999);
    }

    private static void pay(PaymentProcessorFactory factory, String orderId,
                            PaymentMethod method, int amountInr) {
        String prefix = "%s %-11s INR %5d -> ".formatted(orderId, method, amountInr);
        try {
            PaymentResult result = factory.create(method).pay(orderId, amountInr);
            System.out.println(prefix + (result.success()
                    ? "SUCCESS " + result.detail() + " fee=INR " + result.feeInr()
                    : "FAILED  " + result.detail()));
        } catch (UnsupportedPaymentMethodException e) {
            System.out.println(prefix + "ERROR   " + e.getMessage());
        }
    }
}
