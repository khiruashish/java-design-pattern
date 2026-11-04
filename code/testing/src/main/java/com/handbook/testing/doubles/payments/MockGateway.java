package com.handbook.testing.doubles.payments;

import java.util.ArrayList;
import java.util.List;

/** A hand-rolled MOCK: expectations are set up front and verified at the end (what Mockito automates). */
public final class MockGateway implements RefundService.Gateway {

    private final List<String> expected = new ArrayList<>();
    private final List<String> actual = new ArrayList<>();

    public MockGateway expect(String call) { // (1)!
        expected.add(call);
        return this;
    }

    @Override
    public void refund(String paymentId, long paise) {
        actual.add("refund(" + paymentId + ", " + paise + ")");
    }

    @Override
    public void capture(String paymentId, long paise) {
        actual.add("capture(" + paymentId + ", " + paise + ")");
    }

    public String verify() { // (2)!
        return expected.equals(actual) ? "verified " + actual : "FAILED: expected " + expected + " but got " + actual;
    }
}
