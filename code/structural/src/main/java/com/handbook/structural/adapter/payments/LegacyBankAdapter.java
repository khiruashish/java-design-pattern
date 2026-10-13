package com.handbook.structural.adapter.payments;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/** Adapter: implements our interface, translates every call to the vendor SDK. */
public class LegacyBankAdapter implements PaymentGateway {

    private static final String INR_NUMERIC = "356";
    private static final Map<Integer, String> CODES = Map.of(
            0, "Approved", 51, "Declined: amount above limit", 91, "Declined: currency not supported");

    private final LegacyBankGateway legacy; // (1)!

    public LegacyBankAdapter(LegacyBankGateway legacy) {
        this.legacy = legacy;
    }

    @Override
    public ChargeResult charge(String orderId, BigDecimal amountInr) {
        String paise = amountInr.setScale(2, RoundingMode.UNNECESSARY) // (2)!
                .movePointRight(2).toPlainString();
        int code = legacy.doTxn(orderId, paise, INR_NUMERIC);
        String message = CODES.getOrDefault(code, "Declined: code " + code); // (3)!
        String txnId = code == 0 ? legacy.lastTxnRef(orderId) : null;
        return new ChargeResult(code == 0, txnId, message);
    }
}
