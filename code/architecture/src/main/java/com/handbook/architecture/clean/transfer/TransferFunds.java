package com.handbook.architecture.clean.transfer;

import java.util.HashMap;
import java.util.Map;

/**
 * Banking transfer in Clean Architecture terms. The entity enforces account rules; the interactor
 * orchestrates; TWO presenters render the same response model for different audiences.
 */
public final class TransferFunds {

    private TransferFunds() {
    }

    /** Entity: rules that hold for every application the bank builds. */
    public static final class Account {
        private final String id;
        private long balance;
        private long withdrawnToday;
        private final long dailyLimit;

        public Account(String id, long balance, long dailyLimit) {
            this.id = id;
            this.balance = balance;
            this.dailyLimit = dailyLimit;
        }

        void withdraw(long paise) {
            if (paise > balance) {
                throw new IllegalStateException("insufficient funds");
            }
            if (withdrawnToday + paise > dailyLimit) {
                throw new IllegalStateException("daily transfer limit exceeded");
            }
            balance -= paise;
            withdrawnToday += paise;
        }

        void deposit(long paise) {
            balance += paise;
        }

        public long balance() {
            return balance;
        }

        public String id() {
            return id;
        }
    }

    public record Request(String from, String to, long paise, String channel) {
    }

    public record Response(boolean ok, String from, String to, long paise, String reason, long fromBalanceAfter) {
    }

    public interface Presenter {
        void present(Response response);
    }

    public interface AccountGateway {
        Account load(String id);

        void save(Account account);
    }

    /** Interactor: application-specific orchestration. Knows nothing about HTTP or logs. */
    public static final class Interactor {
        private final AccountGateway accounts;

        public Interactor(AccountGateway accounts) {
            this.accounts = accounts;
        }

        public void transfer(Request r, Presenter presenter) {
            Account from = accounts.load(r.from());
            Account to = accounts.load(r.to());
            try {
                from.withdraw(r.paise()); // (1)!
                to.deposit(r.paise());
                accounts.save(from);
                accounts.save(to);
                presenter.present(new Response(true, r.from(), r.to(), r.paise(), "", from.balance()));
            } catch (IllegalStateException rule) {
                presenter.present(new Response(false, r.from(), r.to(), r.paise(), rule.getMessage(), from.balance()));
            }
        }
    }

    /** Presenter 1: the mobile API, with HTTP status and customer-friendly text. */
    public static final class ApiPresenter implements Presenter {
        public String output;

        @Override
        public void present(Response r) {
            output = r.ok() ? "200 {\"message\":\"Sent ₹" + r.paise() / 100 + "\"}"
                    : "422 {\"message\":\"Transfer failed: " + r.reason() + "\"}";
        }
    }

    /** Presenter 2: the compliance audit log, with everything an auditor needs. */
    public static final class AuditPresenter implements Presenter {
        public String output;

        @Override
        public void present(Response r) {
            output = String.format("AUDIT result=%s from=%s to=%s amount=%d reason=%s balance_after=%d",
                    r.ok() ? "OK" : "REJECTED", r.from(), r.to(), r.paise(), r.reason().isEmpty() ? "-" : r.reason(),
                    r.fromBalanceAfter());
        }
    }

    /** Gateway implementation (interface-adapters circle). */
    public static final class InMemoryAccounts implements AccountGateway {
        private final Map<String, Account> accounts = new HashMap<>();

        public InMemoryAccounts with(Account a) {
            accounts.put(a.id(), a);
            return this;
        }

        @Override
        public Account load(String id) {
            return accounts.get(id);
        }

        @Override
        public void save(Account account) {
            accounts.put(account.id(), account);
        }
    }
}
