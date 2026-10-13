package com.handbook.architecture.hexagonal.checkout.adapter.out;

import com.handbook.architecture.hexagonal.checkout.port.out.PaymentPort;

import java.util.Map;
import java.util.function.Function;

/**
 * Driven adapter for a (fictional) payment provider "PayFast". It speaks PayFast's language:
 * rupee strings, a merchant key, and status codes, and translates back to the port's boolean.
 */
public class PayFastPaymentAdapter implements PaymentPort {

    private final Function<Map<String, String>, Map<String, String>> payFastApi; // the HTTP client

    public PayFastPaymentAdapter(Function<Map<String, String>, Map<String, String>> payFastApi) {
        this.payFastApi = payFastApi;
    }

    @Override
    public boolean charge(String customerId, long amountPaise, String reference) {
        Map<String, String> response = payFastApi.apply(Map.of(
                "merchant_key", "MK-GROCERY", // (1)!
                "amount", String.format("%d.%02d", amountPaise / 100, amountPaise % 100),
                "customer_ref", customerId,
                "txn_ref", reference));
        return "CAPTURED".equals(response.get("status")); // (2)!
    }
}
