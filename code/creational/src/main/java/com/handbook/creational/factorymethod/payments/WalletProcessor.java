package com.handbook.creational.factorymethod.payments;

public class WalletProcessor implements PaymentProcessor {

    private static final int WALLET_LIMIT_INR = 10_000;

    @Override
    public PaymentResult pay(String orderId, int amountInr) {
        if (amountInr > WALLET_LIMIT_INR) {
            return PaymentResult.failed("wallet limit INR " + WALLET_LIMIT_INR + " exceeded");
        }
        return PaymentResult.ok(0, "WAL-" + orderId);
    }
}
