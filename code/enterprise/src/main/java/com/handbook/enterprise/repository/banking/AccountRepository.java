package com.handbook.enterprise.repository.banking;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Repository that protects aggregates with optimistic locking (a version column). */
public final class AccountRepository {

    public record Account(String id, long balancePaise, long version) {
        public Account withdraw(long paise) {
            if (paise > balancePaise) {
                throw new IllegalArgumentException("insufficient funds");
            }
            return new Account(id, balancePaise - paise, version);
        }
    }

    private final Map<String, Account> table = new HashMap<>();

    public synchronized Optional<Account> findById(String id) {
        return Optional.ofNullable(table.get(id));
    }

    /** Saves only if nobody else saved since we loaded: UPDATE ... WHERE id = ? AND version = ? */
    public synchronized Account save(Account changed) { // (1)!
        Account current = table.get(changed.id());
        long expected = current == null ? 0 : current.version();
        if (changed.version() != expected) {
            throw new ConcurrentModificationException("Account " + changed.id() + " was modified (expected v"
                    + changed.version() + ", found v" + expected + ")");
        }
        Account stored = new Account(changed.id(), changed.balancePaise(), expected + 1);
        table.put(stored.id(), stored);
        return stored;
    }
}
