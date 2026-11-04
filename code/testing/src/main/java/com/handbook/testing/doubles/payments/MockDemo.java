package com.handbook.testing.doubles.payments;

public class MockDemo {

    public static void main(String[] args) {
        System.out.println("=== Payments: Verifying Calls to the Gateway with a Mock ===");
        MockGateway mock = new MockGateway().expect("refund(pay_1, 15000)");
        new RefundService(mock).refundMissingItems("pay_1", 120_000, 0, 15_000);
        System.out.println("Missing curd (INR 150)        : " + mock.verify());

        MockGateway capped = new MockGateway().expect("refund(pay_2, 5000)");
        new RefundService(capped).refundMissingItems("pay_2", 120_000, 115_000, 15_000);
        System.out.println("Only INR 50 left to refund    : " + capped.verify());

        MockGateway none = new MockGateway();
        new RefundService(none).refundMissingItems("pay_3", 120_000, 120_000, 15_000);
        System.out.println("Fully refunded already        : " + none.verify() + " (no gateway call at all)");
        System.out.println("Mockito equivalent: verify(gateway).refund(\"pay_2\", 5000L); verifyNoMoreInteractions(gateway);");
    }
}
