package com.handbook.microservices.materializedview.balances;

import com.handbook.microservices.materializedview.balances.BalanceView.LedgerEntry;

import java.util.List;
import java.util.TreeMap;

public class BalanceViewDemo {

    public static void main(String[] args) {
        System.out.println("=== Banking: Account Balances Maintained Incrementally from Ledger Events ===");
        BalanceView view = new BalanceView();
        List<LedgerEntry> stream = List.of(
                new LedgerEntry("e1", "SB-1", 500_000),
                new LedgerEntry("e2", "SB-2", 120_000),
                new LedgerEntry("e3", "SB-1", -75_000),
                new LedgerEntry("e3", "SB-1", -75_000), // redelivered by the broker
                new LedgerEntry("e4", "SB-2", -20_000));
        int ignored = 0;
        for (LedgerEntry e : stream) {
            ignored += view.apply(e) ? 0 : 1;
        }
        System.out.println("Applied " + (stream.size() - ignored) + " events, ignored " + ignored + " duplicate");
        System.out.println("Balances: " + new TreeMap<>(view.all()));
        System.out.println("Balance lookup is O(1): no SUM over millions of ledger rows per app open.");
    }
}
