package com.handbook.creational.singleton.payments;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Issues unique, ordered transaction IDs for one payment node.
 * Uses the Initialization-on-demand Holder idiom: lazy, thread-safe, no locks.
 */
public final class TransactionIdGenerator {

    private final AtomicLong sequence = new AtomicLong(); // (1)!
    private final String branchCode;

    private TransactionIdGenerator(String branchCode) { // (2)!
        this.branchCode = branchCode;
    }

    private static final class Holder { // (3)!
        private static final TransactionIdGenerator INSTANCE = new TransactionIdGenerator("BLR");
    }

    public static TransactionIdGenerator getInstance() {
        return Holder.INSTANCE; // (4)!
    }

    public String nextId() {
        return "TXN-%s-%07d".formatted(branchCode, sequence.incrementAndGet());
    }
}
