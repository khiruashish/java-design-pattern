package com.handbook.structural.decorator.payments;

public interface PaymentProcessor {

    String pay(String orderId, int amountInr);
}
