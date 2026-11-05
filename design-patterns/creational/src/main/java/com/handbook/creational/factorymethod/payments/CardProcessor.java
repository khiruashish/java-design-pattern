package com.handbook.creational.factorymethod.payments;

public class CardProcessor implements PaymentProcessor {

    private static final int FEE_PERCENT = 2;

    @Override
    public PaymentResult pay(String orderId, int amountInr) {
        return PaymentResult.ok(amountInr * FEE_PERCENT / 100, "CARD-" + orderId);
    }
}
