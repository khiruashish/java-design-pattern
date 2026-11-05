package com.handbook.structural.adapter.payments;

import java.math.BigDecimal;

/** Target: the interface our checkout code is written against. */
public interface PaymentGateway {

    record ChargeResult(boolean approved, String transactionId, String message) {
    }

    ChargeResult charge(String orderId, BigDecimal amountInr);
}
