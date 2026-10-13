package com.handbook.lld.splitwise;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

/**
 * Splitwise-style expense sharing. Patterns: Strategy (how an expense is split), and a greedy
 * "settle up" that minimises the number of payments by matching biggest creditor with biggest debtor.
 */
public final class ExpenseSharing {

    /** Strategy: who owes how much (in paise) of an amount. */
    public interface SplitStrategy {
        Map<String, Long> split(long amountPaise, List<String> participants);
    }

    public static SplitStrategy equal() {
        return (amount, people) -> {
            Map<String, Long> shares = new TreeMap<>();
            long base = amount / people.size();
            long remainder = amount % people.size();
            for (int i = 0; i < people.size(); i++) {
                shares.put(people.get(i), base + (i < remainder ? 1 : 0)); // spread leftover paise
            }
            return shares;
        };
    }

    public static SplitStrategy percent(Map<String, Integer> percents) {
        return (amount, people) -> {
            if (percents.values().stream().mapToInt(Integer::intValue).sum() != 100) {
                throw new IllegalArgumentException("percentages must add up to 100");
            }
            Map<String, Long> shares = new TreeMap<>();
            long assigned = 0;
            for (int i = 0; i < people.size(); i++) {
                String p = people.get(i);
                long share = i == people.size() - 1 ? amount - assigned : amount * percents.get(p) / 100;
                shares.put(p, share);
                assigned += share;
            }
            return shares;
        };
    }

    public static SplitStrategy exact(Map<String, Long> amounts) {
        return (amount, people) -> {
            if (amounts.values().stream().mapToLong(Long::longValue).sum() != amount) {
                throw new IllegalArgumentException("exact shares must add up to the total");
            }
            return new TreeMap<>(amounts);
        };
    }

    private final Map<String, Long> balance = new TreeMap<>(); // + is owed money, - owes money

    public void addExpense(String paidBy, long amountPaise, List<String> participants, SplitStrategy split) {
        balance.merge(paidBy, amountPaise, Long::sum);
        split.split(amountPaise, participants).forEach((p, share) -> balance.merge(p, -share, Long::sum));
    }

    public Map<String, Long> balances() {
        return new TreeMap<>(balance);
    }

    /** Greedy settlement: at most n-1 payments. */
    public List<String> settleUp() {
        PriorityQueue<long[]> creditors = new PriorityQueue<>((a, b) -> Long.compare(b[1], a[1]));
        PriorityQueue<long[]> debtors = new PriorityQueue<>((a, b) -> Long.compare(b[1], a[1]));
        List<String> names = new ArrayList<>(balance.keySet());
        for (int i = 0; i < names.size(); i++) {
            long b = balance.get(names.get(i));
            if (b > 0) {
                creditors.add(new long[] {i, b});
            } else if (b < 0) {
                debtors.add(new long[] {i, -b});
            }
        }
        List<String> payments = new ArrayList<>();
        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            long[] c = creditors.poll();
            long[] d = debtors.poll();
            long amount = Math.min(c[1], d[1]);
            payments.add(String.format("%s pays %s ₹%,.2f", names.get((int) d[0]), names.get((int) c[0]), amount / 100.0));
            if (c[1] > amount) {
                creditors.add(new long[] {c[0], c[1] - amount});
            }
            if (d[1] > amount) {
                debtors.add(new long[] {d[0], d[1] - amount});
            }
        }
        return payments;
    }
}
