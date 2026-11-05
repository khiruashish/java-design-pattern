package com.handbook.microservices.materializedview.rebuild;

import com.handbook.microservices.materializedview.balances.BalanceView;
import com.handbook.microservices.materializedview.balances.BalanceView.LedgerEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Coding challenge: detect drift by rebuilding the view from the source of truth and comparing. */
public class RebuildCheckDemo {

    public static void main(String[] args) {
        System.out.println("=== Challenge: Rebuild and Compare to Catch View Drift ===");
        List<LedgerEntry> ledger = new ArrayList<>();
        for (int i = 0; i < 1_000; i++) {
            ledger.add(new LedgerEntry("e" + i, "SB-" + (i % 3), i % 2 == 0 ? 10_000 : -4_000));
        }

        BalanceView live = new BalanceView();
        for (LedgerEntry e : ledger) {
            if (!e.eventId().equals("e501")) { // a consumer crash skipped one event months ago
                live.apply(e);
            }
        }

        BalanceView rebuilt = new BalanceView();
        ledger.forEach(rebuilt::apply); // replay from the beginning into a fresh view

        Map<String, String> drift = new TreeMap<>();
        rebuilt.all().forEach((acc, expected) -> {
            long actual = live.balance(acc);
            if (actual != expected) {
                drift.put(acc, "live=" + actual + " expected=" + expected);
            }
        });
        System.out.println("Accounts checked: " + rebuilt.all().size() + ", drifted: " + drift);
        System.out.println("Fix: swap in the rebuilt view (blue/green tables), then alert on any future drift.");
    }
}
