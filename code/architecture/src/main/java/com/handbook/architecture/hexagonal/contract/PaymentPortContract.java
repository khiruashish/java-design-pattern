package com.handbook.architecture.hexagonal.contract;

import com.handbook.architecture.hexagonal.checkout.port.out.PaymentPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Coding challenge: a contract every PaymentPort adapter must satisfy, run against the real adapter
 * (with a stubbed HTTP API) and the in-memory test adapter. If both pass, tests of the core that use
 * the fake are trustworthy.
 */
public final class PaymentPortContract {

    private PaymentPortContract() {
    }

    /** Assumes the adapter's environment accepts amounts up to ₹5,000 and declines above. */
    public static List<String> verify(String name, PaymentPort adapter) {
        List<String> results = new ArrayList<>();
        check(results, name, "small payment is captured", adapter.charge("C1", 10_000, "R1"));
        check(results, name, "payment above the limit is declined", !adapter.charge("C1", 900_000, "R2"));
        check(results, name, "boundary ₹5,000.00 is captured", adapter.charge("C1", 500_000, "R3"));
        return results;
    }

    private static void check(List<String> results, String adapter, String rule, boolean ok) {
        results.add((ok ? "PASS " : "FAIL ") + adapter + ": " + rule);
    }
}
