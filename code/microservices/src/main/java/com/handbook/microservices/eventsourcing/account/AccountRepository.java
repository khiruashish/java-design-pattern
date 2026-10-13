package com.handbook.microservices.eventsourcing.account;

import com.handbook.microservices.eventsourcing.account.Account.Event;
import com.handbook.microservices.eventsourcing.account.Account.Snapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/** Loads aggregates from snapshot + later events, and saves new events with optimistic locking. */
public final class AccountRepository {

    private final EventStore<Event> store;
    private final Map<String, Snapshot> snapshots = new HashMap<>();
    private final int snapshotEvery;

    public AccountRepository(EventStore<Event> store, int snapshotEvery) {
        this.store = store;
        this.snapshotEvery = snapshotEvery;
    }

    public Account load(String id) {
        Snapshot snap = snapshots.get(id);
        return Account.replay(snap, store.read(id, snap == null ? 0 : snap.version()));
    }

    /** Load, run the command, append its events at the version we loaded. */
    public void execute(String id, Function<Account, List<Event>> command) {
        Account account = load(id);
        List<Event> events = command.apply(account);
        store.append(id, account.version(), events);
        Account after = Account.replay(account.snapshot(), events);
        if (after.version() / snapshotEvery > account.version() / snapshotEvery) { // (1)!
            snapshots.put(id, after.snapshot());
        }
    }
}
