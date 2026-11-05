package com.handbook.structural.adapter.payments;

/**
 * Adaptee: a vendor SDK we cannot change. Amounts are strings in paise,
 * results are numeric codes, and the reference comes back in a separate call.
 */
public class LegacyBankGateway {

    public int doTxn(String merchantRef, String amountPaise, String currencyCode) {
        long paise = Long.parseLong(amountPaise);
        if (!"356".equals(currencyCode)) { // ISO 4217 numeric code for INR
            return 91; // unsupported currency
        }
        return paise > 5_000_000 ? 51 : 0; // 51 = insufficient funds / limit exceeded
    }

    public String lastTxnRef(String merchantRef) {
        return "LB" + Math.abs(merchantRef.hashCode() % 100_000);
    }
}
