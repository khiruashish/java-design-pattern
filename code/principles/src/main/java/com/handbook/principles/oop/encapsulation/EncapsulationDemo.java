package com.handbook.principles.oop.encapsulation;

public class EncapsulationDemo {
    public static void main(String[] args) {
        BankAccount account = new BankAccount("ACC-1", 10_000);
        account.deposit(2_500);
        account.withdraw(4_000);
        System.out.println("balance = " + account.balanceCents());
        try {
            account.withdraw(1_000_000);
        } catch (IllegalStateException e) {
            System.out.println("rejected: " + e.getMessage());
        }
        try {
            account.history().add("withdraw 999999");
        } catch (UnsupportedOperationException e) {
            System.out.println("history is read-only to callers");
        }
        System.out.println("history = " + account.history());
    }
}
