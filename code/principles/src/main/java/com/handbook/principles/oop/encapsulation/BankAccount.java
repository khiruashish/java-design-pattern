package com.handbook.principles.oop.encapsulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulation: state is private and can only change through methods that protect the invariant
 * "balance is never negative". Callers cannot reach in and set the balance directly.
 */
public class BankAccount {
    private final String id;
    private long balanceCents;
    private final List<String> history = new ArrayList<>();

    public BankAccount(String id, long openingCents) {
        if (openingCents < 0) throw new IllegalArgumentException("opening balance must be >= 0");
        this.id = id;
        this.balanceCents = openingCents;
        history.add("open " + openingCents);
    }

    public void deposit(long cents) {
        if (cents <= 0) throw new IllegalArgumentException("deposit must be positive");
        balanceCents += cents;
        history.add("deposit " + cents);
    }

    public void withdraw(long cents) {
        if (cents <= 0) throw new IllegalArgumentException("withdrawal must be positive");
        if (cents > balanceCents) throw new IllegalStateException("insufficient funds: balance " + balanceCents + ", requested " + cents);
        balanceCents -= cents;
        history.add("withdraw " + cents);
    }

    public long balanceCents() { return balanceCents; }

    public String id() { return id; }

    /** Read-only view: callers can look at the history but cannot rewrite it. */
    public List<String> history() { return Collections.unmodifiableList(history); }
}
