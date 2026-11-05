package com.handbook.structural.decorator.payments;

public class LoggingProcessor extends PaymentDecorator {

    public LoggingProcessor(PaymentProcessor inner) {
        super(inner);
    }

    @Override
    public String pay(String orderId, int amountInr) {
        System.out.println("  [log] -> pay " + orderId + " INR " + amountInr);
        try {
            String result = super.pay(orderId, amountInr);
            System.out.println("  [log] <- " + result);
            return result;
        } catch (RuntimeException e) {
            System.out.println("  [log] <- FAILED: " + e.getMessage());
            throw e;
        }
    }
}
