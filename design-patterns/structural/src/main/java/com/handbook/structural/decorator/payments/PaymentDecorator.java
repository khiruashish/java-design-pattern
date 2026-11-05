package com.handbook.structural.decorator.payments;

/** Base decorator: implements the interface and forwards to the wrapped processor. */
public abstract class PaymentDecorator implements PaymentProcessor {

    protected final PaymentProcessor inner; // (1)!

    protected PaymentDecorator(PaymentProcessor inner) {
        this.inner = inner;
    }

    @Override
    public String pay(String orderId, int amountInr) {
        return inner.pay(orderId, amountInr);
    }
}
