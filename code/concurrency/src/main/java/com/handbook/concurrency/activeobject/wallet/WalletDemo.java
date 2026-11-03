package com.handbook.concurrency.activeobject.wallet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class WalletDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Payments: A Wallet as an Active Object ===");
        try (WalletActor wallet = new WalletActor(); var callers = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<Boolean>> debits = new ArrayList<>();
            for (int i = 0; i < 1_000; i++) {
                callers.submit(() -> wallet.credit(100));                    // 1,000 cashback credits of INR 1
            }
            callers.close();                                                 // all credits enqueued
            for (int i = 0; i < 15; i++) {
                debits.add(wallet.debit(10_000));                           // 15 attempts to spend INR 100
            }
            long approved = debits.stream().filter(CompletableFuture::join).count();
            System.out.println("Debits of INR 100 approved: " + approved + " of 15 (balance allows exactly 10)");
            System.out.println("Statement: " + wallet.statement().get());
        }
    }
}
