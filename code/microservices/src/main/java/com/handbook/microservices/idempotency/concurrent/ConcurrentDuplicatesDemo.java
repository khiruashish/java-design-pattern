package com.handbook.microservices.idempotency.concurrent;

import com.handbook.microservices.idempotency.wallet.WalletCreditConsumer;
import com.handbook.microservices.support.Db;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Coding challenge: the same message is processed by 8 consumers AT THE SAME TIME (e.g. after a
 * rebalance two instances both hold it). A check-then-act "if not processed then process" in
 * application code would let several through; the database unique constraint lets exactly one win.
 */
public class ConcurrentDuplicatesDemo {

    public static void main(String[] args) throws Exception {
        DataSource db = Db.inMemory("wallet_race", WalletCreditConsumer.SCHEMA);
        var consumer = new WalletCreditConsumer(db);
        int racers = 8;
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger applied = new AtomicInteger();
        AtomicInteger skipped = new AtomicInteger();
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < racers; i++) {
            threads.add(Thread.ofPlatform().start(() -> {
                try {
                    start.await();
                    if (consumer.onRefundIssued("msg-900", "CUST-77", 50_000)) {
                        applied.incrementAndGet();
                    } else {
                        skipped.incrementAndGet();
                    }
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }));
        }
        start.countDown();
        for (Thread t : threads) {
            t.join();
        }
        System.out.println("=== Challenge: 8 concurrent deliveries of msg-900 (₹500 refund) ===");
        System.out.println("applied: " + applied.get() + ", skipped as duplicate: " + skipped.get());
        System.out.println("wallet balance: ₹" + Db.count(db, "SELECT balance FROM wallets WHERE customer = 'CUST-77'") / 100);
    }
}
