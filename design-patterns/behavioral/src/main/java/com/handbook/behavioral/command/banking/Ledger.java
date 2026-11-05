package com.handbook.behavioral.command.banking;

import java.util.HashMap;
import java.util.Map;

/** Receiver: the actual money movement. */
public class Ledger {

    private final Map<String, Long> balances = new HashMap<>(Map.of("ACC-1", 10_000L, "ACC-2", 500L));

    public void debit(String account, long amount) {
        long balance = balances.getOrDefault(account, 0L);
        if (balance < amount) {
            throw new IllegalStateException("insufficient funds in " + account);
        }
        balances.put(account, balance - amount);
    }

    public void credit(String account, long amount) {
        balances.merge(account, amount, Long::sum);
    }

    public long balance(String account) {
        return balances.getOrDefault(account, 0L);
    }
}
