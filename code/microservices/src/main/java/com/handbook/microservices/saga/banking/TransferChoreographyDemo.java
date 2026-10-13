package com.handbook.microservices.saga.banking;

import com.handbook.microservices.saga.banking.TransferChoreography.TransferRequested;
import com.handbook.microservices.support.EventBus;

import java.util.Set;

public class TransferChoreographyDemo {

    public static void main(String[] args) {
        EventBus bus = new EventBus(true);
        var transfers = new TransferChoreography(bus, Set.of("B-CLOSED-9"));
        transfers.openA("A-100", 5_000);

        System.out.println("=== Banking: inter-bank transfer as a choreographed saga ===");
        bus.publish("transfers", new TransferRequested("TX-1", "A-100", "B-200", 1_500));
        System.out.println("A-100 balance: " + transfers.balanceA("A-100"));

        System.out.println();
        bus.publish("transfers", new TransferRequested("TX-2", "A-100", "B-CLOSED-9", 2_000));
        System.out.println("A-100 balance: " + transfers.balanceA("A-100") + " (debit compensated by refund)");

        System.out.println();
        bus.publish("transfers", new TransferRequested("TX-3", "A-100", "B-200", 9_999));
        System.out.println("A-100 balance: " + transfers.balanceA("A-100"));
    }
}
