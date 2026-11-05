package com.handbook.testing.testdata.ledger;

import com.handbook.testing.testdata.ledger.RandomTransfers.Transfer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LedgerPropertyDemo {

    /** System under test, with a bug: credits above INR 5,000 are rounded down to an even paisa, leaking money. */
    static Map<String, Long> apply(List<Transfer> transfers, boolean buggy) {
        Map<String, Long> balances = new HashMap<>();
        for (Transfer t : transfers) {
            long credited = buggy && t.paise() > 500_000 ? t.paise() - (t.paise() % 2) : t.paise();
            balances.merge(t.from(), -t.paise(), Long::sum);
            balances.merge(t.to(), credited, Long::sum);
        }
        return balances;
    }

    public static void main(String[] args) {
        System.out.println("=== Banking: A Ledger Invariant Checked on 10,000 Generated Transfers ===");
        List<String> accounts = List.of("A", "B", "C", "D", "E");
        long seed = 20261101L;
        List<Transfer> transfers = RandomTransfers.generate(seed, 10_000, accounts);
        for (boolean buggy : new boolean[] {false, true}) {
            long sum = apply(transfers, buggy).values().stream().mapToLong(Long::longValue).sum();
            System.out.println((buggy ? "buggy ledger  " : "correct ledger") + ": sum of all balances = " + sum
                    + (sum == 0 ? " -> invariant holds" : " -> INVARIANT BROKEN, replay with seed " + seed));
        }
        System.out.println("Same seed, same 10,000 transfers: first transfer is always " + transfers.get(0));
    }
}
