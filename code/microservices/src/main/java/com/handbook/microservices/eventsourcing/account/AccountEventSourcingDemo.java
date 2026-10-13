package com.handbook.microservices.eventsourcing.account;

import com.handbook.microservices.eventsourcing.account.Account.Event;
import com.handbook.microservices.eventsourcing.account.Account.Opened;

import java.util.List;

public class AccountEventSourcingDemo {

    public static void main(String[] args) {
        EventStore<Event> store = new EventStore<>();
        var repo = new AccountRepository(store, 4);

        System.out.println("=== Banking: an event-sourced account ===");
        store.append("ACC-1", 0, List.of(new Opened("Asha")));
        repo.execute("ACC-1", a -> a.deposit(10_000));
        repo.execute("ACC-1", a -> a.withdraw(2_500));
        repo.execute("ACC-1", a -> a.deposit(4_000));
        repo.execute("ACC-1", a -> a.withdraw(1_000));
        repo.execute("ACC-1", a -> a.deposit(700));

        System.out.println("Event stream : " + store.read("ACC-1", 0));
        int before = store.eventsReadSoFar();
        Account loaded = repo.load("ACC-1");
        System.out.println("Balance " + loaded.balance() + " at version " + loaded.version()
                + ", rebuilt by replaying " + (store.eventsReadSoFar() - before) + " event(s) after the snapshot at v4");

        System.out.println("-- two tellers load v6 at the same time --");
        Account tellerA = repo.load("ACC-1");
        Account tellerB = repo.load("ACC-1");
        store.append("ACC-1", tellerA.version(), tellerA.withdraw(10_000));
        System.out.println("Teller A withdrew 10000 -> balance " + repo.load("ACC-1").balance());
        try {
            store.append("ACC-1", tellerB.version(), tellerB.withdraw(10_000));
        } catch (EventStore.ConcurrencyException e) {
            System.out.println("Teller B rejected: " + e.getMessage() + " -> reload and retry");
            try {
                repo.execute("ACC-1", a -> a.withdraw(10_000));
            } catch (IllegalStateException retryFailed) {
                System.out.println("Retry on fresh state: " + retryFailed.getMessage() + " (no double spend)");
            }
        }
    }
}
