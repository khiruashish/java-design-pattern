package com.handbook.creational.factorymethod.payments;

public class UpiProcessor implements PaymentProcessor {

    @Override
    public PaymentResult pay(String orderId, int amountInr) {
        return PaymentResult.ok(0, "UPI-" + orderId); // UPI has no merchant fee
    }
}
