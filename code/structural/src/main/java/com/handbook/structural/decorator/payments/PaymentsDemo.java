package com.handbook.structural.decorator.payments;

public class PaymentsDemo {

    public static void main(String[] args) {
        System.out.println("=== Payments: Stacking Cross-Cutting Concerns ===");

        PaymentProcessor processor =
                new FraudCheckProcessor(                // outermost: cheapest check first
                        new RetryingProcessor(
                                new LoggingProcessor(   // logs every attempt
                                        new FlakyGatewayProcessor(2)),
                                3),
                        50_000);

        System.out.println("Order 1:");
        System.out.println("Result: " + processor.pay("ORD-1", 1_200));
        System.out.println("Order 2:");
        System.out.println("Result: " + processor.pay("ORD-2", 75_000));
    }
}
