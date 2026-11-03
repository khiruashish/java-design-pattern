package com.handbook.microservices.sharding.banking;

public class LedgerShardingDemo {

    public static void main(String[] args) {
        System.out.println("=== Banking: Accounts Hash-Sharded Across 3 Databases ===");
        ShardedLedger ledger = new ShardedLedger(3);
        String[] accounts = {"ACC-1001", "ACC-1002", "ACC-1003", "ACC-1004", "ACC-1007"};
        for (String a : accounts) {
            ledger.open(a, 100_000);
            System.out.println(a + " -> shard " + ledger.shardOf(a));
        }
        System.out.println("ACC-1001 -> ACC-1004: " + ledger.transfer("ACC-1001", "ACC-1004", 25_000));
        System.out.println("  balances now " + ledger.balance("ACC-1001") + " / " + ledger.balance("ACC-1004"));
        System.out.println("ACC-1001 -> ACC-1002: " + ledger.transfer("ACC-1001", "ACC-1002", 10_000));
    }
}
