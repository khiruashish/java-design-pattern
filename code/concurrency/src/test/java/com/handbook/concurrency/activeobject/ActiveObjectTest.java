package com.handbook.concurrency.activeobject;

import com.handbook.concurrency.activeobject.generic.Actor;
import com.handbook.concurrency.activeobject.keyed.KeyedSerialExecutor;
import com.handbook.concurrency.activeobject.printer.LabelPrinter;
import com.handbook.concurrency.activeobject.wallet.WalletActor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

class ActiveObjectTest {

    @Test
    void walletNeverOverdrawsUnderConcurrency() throws Exception {
        try (WalletActor w = new WalletActor(); var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            w.credit(500).get();
            List<CompletableFuture<Boolean>> results = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                results.add(pool.submit(() -> w.debit(100)).get());
            }
            assertThat(results.stream().filter(CompletableFuture::join).count()).isEqualTo(5);
        }
    }

    @Test
    void fullPrinterQueueRejectsInsteadOfBlocking() {
        CountDownLatch paper = new CountDownLatch(1);
        try (LabelPrinter p = new LabelPrinter(1, paper)) {
            p.print("a");
            p.print("b");
            CompletableFuture<Integer> third = p.print("c");
            paper.countDown();
            assertThat(third).isCompletedExceptionally();
        }
    }

    @Test
    void keyedExecutorPreservesPerKeyOrder() {
        List<Integer> seen = java.util.Collections.synchronizedList(new ArrayList<>());
        try (var pool = Executors.newFixedThreadPool(4)) {
            KeyedSerialExecutor ex = new KeyedSerialExecutor(pool);
            List<CompletableFuture<?>> all = new ArrayList<>();
            for (int i = 0; i < 200; i++) {
                int n = i;
                all.add(ex.submit("k", () -> seen.add(n)));
            }
            CompletableFuture.allOf(all.toArray(CompletableFuture[]::new)).join();
        }
        assertThat(seen).isSorted().hasSize(200);
    }

    @Test
    void actorSerializesAccessToPlainState() throws Exception {
        try (Actor<int[]> a = new Actor<>(new int[1]); var pool = Executors.newFixedThreadPool(4)) {
            for (int i = 0; i < 1_000; i++) {
                pool.submit(() -> a.ask(s -> ++s[0]));
            }
            pool.close();
            assertThat(a.ask(s -> s[0]).get()).isEqualTo(1_000);
        }
    }
}
