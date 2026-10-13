package com.handbook.microservices.cqrs.banking;

import com.handbook.microservices.cqrs.banking.AccountCqrs.AccountCommands;
import com.handbook.microservices.cqrs.banking.AccountCqrs.BalanceView;
import com.handbook.microservices.cqrs.banking.AccountCqrs.MiniStatementView;

public class AccountCqrsDemo {

    public static void main(String[] args) {
        var commands = new AccountCommands();
        var balance = new BalanceView();
        var statement = new MiniStatementView(3);
        commands.subscribe(balance);
        commands.subscribe(statement);

        System.out.println("=== Banking: one write model, two read models ===");
        commands.deposit("ACC-1", 5_000_000, "Salary");
        commands.withdraw("ACC-1", 1_250_000, "Rent");
        commands.withdraw("ACC-1", 89_900, "Groceries");
        commands.withdraw("ACC-1", 45_000, "Electricity");
        try {
            commands.withdraw("ACC-1", 9_999_900, "New laptop");
        } catch (IllegalStateException e) {
            System.out.println("Withdraw ₹99,999 rejected: " + e.getMessage());
        }
        System.out.println("Balance tile   : " + balance.balance("ACC-1"));
        System.out.println("Mini statement :");
        statement.statement("ACC-1").forEach(line -> System.out.println("  " + line));
    }
}
