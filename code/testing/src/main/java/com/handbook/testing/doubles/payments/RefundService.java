package com.handbook.testing.doubles.payments;

/** Partial refunds: refund only what's left, never more than was captured. */
public final class RefundService {

    public interface Gateway {
        void refund(String paymentId, long paise);
        void capture(String paymentId, long paise);
    }

    private final Gateway gateway;

    public RefundService(Gateway gateway) {
        this.gateway = gateway;
    }

    public void refundMissingItems(String paymentId, long capturedPaise, long alreadyRefundedPaise, long missingItemsPaise) {
        long refundable = capturedPaise - alreadyRefundedPaise;
        long amount = Math.min(refundable, missingItemsPaise);
        if (amount > 0) {
            gateway.refund(paymentId, amount);
        }
    }
}
