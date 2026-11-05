package com.handbook.testing.doubles.travel;

import com.handbook.testing.doubles.travel.FaultyFakeGateway.Fault;

public class FaultInjectionDemo {

    public static void main(String[] args) {
        System.out.println("=== Travel: Testing Retry Paths with a Fault-Injecting Fake ===");
        FaultyFakeGateway oneTimeout = new FaultyFakeGateway().failNext(Fault.TIMEOUT);
        System.out.println("1 timeout   -> " + new BookingService(oneTimeout).book("Sea Breeze Goa", "2026-12-24")
                + ", calls=" + oneTimeout.calls());

        FaultyFakeGateway soldOut = new FaultyFakeGateway().failNext(Fault.SOLD_OUT);
        System.out.println("sold out    -> " + new BookingService(soldOut).book("Sea Breeze Goa", "2026-12-24")
                + ", calls=" + soldOut.calls() + " (not retried)");

        FaultyFakeGateway down = new FaultyFakeGateway().failNext(Fault.TIMEOUT).failNext(Fault.TIMEOUT).failNext(Fault.TIMEOUT);
        System.out.println("3 timeouts  -> " + new BookingService(down).book("Sea Breeze Goa", "2026-12-24")
                + ", calls=" + down.calls() + ", holds=" + down.activeHolds());
        System.out.println("These paths are almost impossible to trigger reliably against the real partner sandbox.");
    }
}
