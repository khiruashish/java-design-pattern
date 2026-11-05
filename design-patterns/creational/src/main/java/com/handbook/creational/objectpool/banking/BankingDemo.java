package com.handbook.creational.objectpool.banking;

import java.util.concurrent.TimeoutException;

public class BankingDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Banking: Connection Pool for the Ledger Service ===");
        ConnectionPool pool = new ConnectionPool(2);

        try (var lease = pool.acquire(100)) {
            System.out.println(lease.connection().query("SELECT balance FROM accounts WHERE id=1"));
        }
        try (var lease = pool.acquire(100)) {
            System.out.println(lease.connection().query("SELECT balance FROM accounts WHERE id=2") + "  <- reused");
        }
        System.out.println("Stats: " + pool.stats());

        System.out.println("-- two transfers at once, then a third request --");
        var a = pool.acquire(100);
        var b = pool.acquire(100);
        System.out.println(a.connection().query("BEGIN; UPDATE accounts ..."));
        System.out.println(b.connection().query("BEGIN; UPDATE accounts ..."));
        try {
            pool.acquire(100);
        } catch (TimeoutException e) {
            System.out.println("Third request: " + e.getMessage());
        }
        a.close();
        System.out.println("Returned connection reset (no open transaction)? " + !a.connection().inTransaction());
        b.connection().simulateNetworkDrop();
        b.close();

        System.out.println("-- after a network drop --");
        try (var lease = pool.acquire(100)) {
            System.out.println(lease.connection().query("SELECT 1"));
        }
        try (var lease1 = pool.acquire(100); var lease2 = pool.acquire(100)) {
            System.out.println("Healthy pair: #" + lease1.connection().id() + " and #" + lease2.connection().id());
        }
        System.out.println("Stats: " + pool.stats());
    }
}
