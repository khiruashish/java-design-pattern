package com.handbook.enterprise.valueobject.ids;

import java.util.TreeMap;
import java.util.Map;

/** Coding challenge: cure primitive obsession in transfer(String, String, long). */
public class TypedIdsDemo {

    static final Map<String, Long> BALANCES = new TreeMap<>();

    // Before: three primitives; nothing stops callers from swapping them.
    static void transferRaw(String fromAccount, String toAccount, long paise) {
        BALANCES.merge(fromAccount, -paise, Long::sum);
        BALANCES.merge(toAccount, paise, Long::sum);
    }

    // After: each concept is its own type with its own rules.
    record AccountId(String value) {
        AccountId {
            if (!value.matches("ACC-\\d{4}")) {
                throw new IllegalArgumentException("bad account id " + value);
            }
        }
    }

    record Paise(long value) {
        Paise {
            if (value <= 0) {
                throw new IllegalArgumentException("transfer amount must be positive: " + value);
            }
        }
    }

    static void transfer(AccountId from, AccountId to, Paise amount) {
        transferRaw(from.value(), to.value(), amount.value());
    }

    public static void main(String[] args) {
        System.out.println("=== Challenge: Typed IDs Instead of Strings ===");
        BALANCES.put("ACC-1001", 50_000L);
        BALANCES.put("ACC-2002", 0L);
        String customer = "ACC-1001";
        String merchant = "ACC-2002";
        transferRaw(merchant, customer, 10_000); // swapped by mistake: compiles fine
        System.out.println("Raw strings, swapped args : " + BALANCES);

        BALANCES.put("ACC-1001", 50_000L);
        BALANCES.put("ACC-2002", 0L);
        transfer(new AccountId("ACC-1001"), new AccountId("ACC-2002"), new Paise(10_000));
        System.out.println("Typed values, correct call: " + BALANCES);
        try {
            transfer(new AccountId("ACC-1001"), new AccountId("ACC-2002"), new Paise(-500));
        } catch (IllegalArgumentException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
        System.out.println("transfer(paise, from, to) would not compile: the types make the order part of the API.");
    }
}
