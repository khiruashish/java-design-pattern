package com.handbook.microservices.cqrs.banking;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/** One write model, two read models, each shaped for one screen. */
public final class AccountCqrs {

    public record Transacted(String account, String description, long amountPaise, long balanceAfter) {
    }

    /** Write side: enforces the no-overdraft rule. */
    public static final class AccountCommands {
        private final Map<String, Long> balances = new HashMap<>();
        private final List<Consumer<Transacted>> projections = new ArrayList<>();

        public void subscribe(Consumer<Transacted> projection) {
            projections.add(projection);
        }

        public void deposit(String account, long paise, String description) {
            apply(account, paise, description);
        }

        public void withdraw(String account, long paise, String description) {
            if (balances.getOrDefault(account, 0L) < paise) { // (1)!
                throw new IllegalStateException("insufficient funds");
            }
            apply(account, -paise, description);
        }

        private void apply(String account, long delta, String description) {
            long after = balances.merge(account, delta, Long::sum);
            Transacted event = new Transacted(account, description, delta, after);
            projections.forEach(p -> p.accept(event));
        }
    }

    /** Read model 1: the balance tile on the app home screen (one key lookup). */
    public static final class BalanceView implements Consumer<Transacted> {
        private final Map<String, Long> balances = new HashMap<>();

        @Override
        public void accept(Transacted e) {
            balances.put(e.account(), e.balanceAfter());
        }

        public String balance(String account) {
            return String.format("₹%,.2f", balances.getOrDefault(account, 0L) / 100.0);
        }
    }

    /** Read model 2: the last N transactions, already formatted for the mini-statement. */
    public static final class MiniStatementView implements Consumer<Transacted> {
        private final Map<String, Deque<String>> lines = new HashMap<>();
        private final int size;

        public MiniStatementView(int size) {
            this.size = size;
        }

        @Override
        public void accept(Transacted e) {
            Deque<String> d = lines.computeIfAbsent(e.account(), k -> new ArrayDeque<>());
            d.addFirst(String.format("%-14s %+,10.2f", e.description(), e.amountPaise() / 100.0)); // (2)!
            if (d.size() > size) {
                d.removeLast();
            }
        }

        public List<String> statement(String account) {
            return List.copyOf(lines.getOrDefault(account, new ArrayDeque<>()));
        }
    }
}
