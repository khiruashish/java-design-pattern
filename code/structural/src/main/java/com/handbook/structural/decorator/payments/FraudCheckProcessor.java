package com.handbook.structural.decorator.payments;

public class FraudCheckProcessor extends PaymentDecorator {

    private final int limitInr;

    public FraudCheckProcessor(PaymentProcessor inner, int limitInr) {
        super(inner);
        this.limitInr = limitInr;
    }

    @Override
    public String pay(String orderId, int amountInr) {
        if (amountInr > limitInr) {
            return "BLOCKED " + orderId + " (fraud rule: amount > INR " + limitInr + ")"; // (1)!
        }
        return super.pay(orderId, amountInr);
    }
}
