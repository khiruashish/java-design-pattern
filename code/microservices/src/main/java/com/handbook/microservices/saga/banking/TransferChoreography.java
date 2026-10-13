package com.handbook.microservices.saga.banking;

import com.handbook.microservices.support.EventBus;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A choreography-based saga: no orchestrator. Each service reacts to events and publishes its own.
 * The debit side listens for CreditFailed and refunds, which is the compensation.
 */
public final class TransferChoreography {

    public record TransferRequested(String id, String from, String to, long amount) {
    }

    public record AccountDebited(String id, String from, String to, long amount) {
    }

    public record AccountCredited(String id) {
    }

    public record CreditFailed(String id, String from, long amount, String reason) {
    }

    public record TransferFailed(String id, String reason) {
    }

    /** Owns source accounts (bank A). */
    public static final class DebitService {
        final Map<String, Long> balances = new HashMap<>();

        DebitService(EventBus bus) {
            bus.<TransferRequested>subscribe("transfers", e -> {
                long balance = balances.getOrDefault(e.from(), 0L);
                if (balance < e.amount()) {
                    bus.publish("transfers.failed", new TransferFailed(e.id(), "insufficient funds"));
                    return;
                }
                balances.put(e.from(), balance - e.amount());
                bus.publish("accounts.debited", new AccountDebited(e.id(), e.from(), e.to(), e.amount()));
            });
            bus.<CreditFailed>subscribe("credits.failed", e -> { // (1)!
                balances.merge(e.from(), e.amount(), Long::sum);
                bus.publish("transfers.failed", new TransferFailed(e.id(), e.reason() + ", refunded"));
            });
        }
    }

    /** Owns destination accounts (bank B). */
    public static final class CreditService {
        final Map<String, Long> balances = new HashMap<>();

        CreditService(EventBus bus, Set<String> closedAccounts) {
            bus.<AccountDebited>subscribe("accounts.debited", e -> {
                if (closedAccounts.contains(e.to())) {
                    bus.publish("credits.failed", new CreditFailed(e.id(), e.from(), e.amount(), "account closed"));
                    return;
                }
                balances.merge(e.to(), e.amount(), Long::sum);
                bus.publish("accounts.credited", new AccountCredited(e.id()));
            });
        }
    }

    public final DebitService debit;
    public final CreditService credit;

    public TransferChoreography(EventBus bus, Set<String> closedAccounts) {
        this.debit = new DebitService(bus);
        this.credit = new CreditService(bus, closedAccounts);
    }

    public long balanceA(String account) {
        return debit.balances.getOrDefault(account, 0L);
    }

    public void openA(String account, long balance) {
        debit.balances.put(account, balance);
    }
}
