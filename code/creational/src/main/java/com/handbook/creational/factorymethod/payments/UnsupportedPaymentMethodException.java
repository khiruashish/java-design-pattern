package com.handbook.creational.factorymethod.payments;

public class UnsupportedPaymentMethodException extends RuntimeException {

    public UnsupportedPaymentMethodException(PaymentMethod method) {
        super("No processor registered for " + method);
    }
}
