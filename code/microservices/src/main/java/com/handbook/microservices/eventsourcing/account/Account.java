package com.handbook.microservices.eventsourcing.account;

import java.util.List;

/**
 * An event-sourced aggregate. State is never stored directly: it is the result of applying every
 * event in order. Commands check rules against that state and return NEW events.
 */
public final class Account {

    public sealed interface Event permits Opened, Deposited, Withdrawn {
    }

    public record Opened(String owner) implements Event {
    }

    public record Deposited(long paise) implements Event {
    }

    public record Withdrawn(long paise) implements Event {
    }

    /** A snapshot: the state as of a version, so replay can start there. */
    public record Snapshot(long version, String owner, long balance) {
    }

    private String owner;
    private long balance;
    private long version;

    public static Account replay(Snapshot snapshot, List<Event> laterEvents) {
        Account a = new Account();
        if (snapshot != null) {
            a.owner = snapshot.owner();
            a.balance = snapshot.balance();
            a.version = snapshot.version();
        }
        laterEvents.forEach(a::apply); // (1)!
        return a;
    }

    private void apply(Event e) { // (2)!
        switch (e) {
            case Opened o -> owner = o.owner();
            case Deposited d -> balance += d.paise();
            case Withdrawn w -> balance -= w.paise();
        }
        version++;
    }

    public List<Event> deposit(long paise) { // (3)!
        if (paise <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        return List.of(new Deposited(paise));
    }

    public List<Event> withdraw(long paise) {
        if (paise > balance) {
            throw new IllegalStateException("insufficient funds");
        }
        return List.of(new Withdrawn(paise));
    }

    public Snapshot snapshot() {
        return new Snapshot(version, owner, balance);
    }

    public long version() {
        return version;
    }

    public long balance() {
        return balance;
    }
}
