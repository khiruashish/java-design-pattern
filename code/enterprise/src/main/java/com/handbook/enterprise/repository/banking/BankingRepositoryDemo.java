package com.handbook.enterprise.repository.banking;

import com.handbook.enterprise.repository.banking.AccountRepository.Account;

import java.util.ConcurrentModificationException;

public class BankingRepositoryDemo {

    public static void main(String[] args) {
        System.out.println("=== Banking: Repository with Optimistic Locking ===");
        AccountRepository accounts = new AccountRepository();
        accounts.save(new Account("SB-501", 1_000_000, 0));

        Account atm = accounts.findById("SB-501").orElseThrow();    // two channels load v1
        Account upi = accounts.findById("SB-501").orElseThrow();
        System.out.println("ATM and UPI both loaded " + atm);

        System.out.println("ATM saves  -> " + accounts.save(atm.withdraw(800_000)));
        try {
            accounts.save(upi.withdraw(700_000));                   // based on the stale balance
        } catch (ConcurrentModificationException e) {
            System.out.println("UPI rejected: " + e.getMessage());
            Account fresh = accounts.findById("SB-501").orElseThrow(); // (1)!
            try {
                accounts.save(fresh.withdraw(700_000));
            } catch (IllegalArgumentException again) {
                System.out.println("UPI retried on fresh state: " + again.getMessage() + " (no overdraft)");
            }
        }
    }
}
