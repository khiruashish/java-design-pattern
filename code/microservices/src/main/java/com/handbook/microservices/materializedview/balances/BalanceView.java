package com.handbook.microservices.materializedview.balances;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/** Incrementally maintained view: each ledger event updates the running balance once. */
public final class BalanceView {

    public record LedgerEntry(String eventId, String account, long deltaPaise) { }

    private final Map<String, Long> balances = new TreeMap<>();
    private final Set<String> applied = new HashSet<>(); // in production: a processed_events table

    public boolean apply(LedgerEntry e) {
        if (!applied.add(e.eventId())) { // (1)!
            return false;
        }
        balances.merge(e.account(), e.deltaPaise(), Long::sum); // (2)!
        return true;
    }

    public long balance(String account) {
        return balances.getOrDefault(account, 0L);
    }

    public Map<String, Long> all() {
        return Map.copyOf(balances);
    }
}
