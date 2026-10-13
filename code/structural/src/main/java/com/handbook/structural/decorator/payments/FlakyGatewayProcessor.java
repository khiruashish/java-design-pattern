package com.handbook.structural.decorator.payments;

/** Concrete component: the real gateway call. Fails the first N attempts to simulate timeouts. */
public class FlakyGatewayProcessor implements PaymentProcessor {

    private int failuresLeft;

    public FlakyGatewayProcessor(int failuresBeforeSuccess) {
        this.failuresLeft = failuresBeforeSuccess;
    }

    @Override
    public String pay(String orderId, int amountInr) {
        if (failuresLeft-- > 0) {
            throw new IllegalStateException("gateway timeout");
        }
        return "PAID " + orderId;
    }
}
