package com.handbook.creational.factorymethod.payments;

public record PaymentResult(boolean success, int feeInr, String detail) {

    public static PaymentResult ok(int feeInr, String reference) {
        return new PaymentResult(true, feeInr, "ref=" + reference);
    }

    public static PaymentResult failed(String reason) {
        return new PaymentResult(false, 0, reason);
    }
}
