package com.handbook.structural.adapter.payments;

import java.math.BigDecimal;

/** Client: knows only PaymentGateway. */
public class CheckoutService {

    private final PaymentGateway gateway;

    public CheckoutService(PaymentGateway gateway) {
        this.gateway = gateway;
    }

    public String pay(String orderId, String amountInr) {
        PaymentGateway.ChargeResult r = gateway.charge(orderId, new BigDecimal(amountInr));
        return "%s INR %10s -> %s%s".formatted(orderId, amountInr, r.message(),
                r.approved() ? " (txn " + r.transactionId() + ")" : "");
    }
}
