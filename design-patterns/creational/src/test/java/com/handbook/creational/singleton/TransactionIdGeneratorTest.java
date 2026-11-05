package com.handbook.creational.singleton;

import com.handbook.creational.singleton.payments.TransactionIdGenerator;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionIdGeneratorTest {

    @Test
    void getInstanceAlwaysReturnsSameObject() {
        assertThat(TransactionIdGenerator.getInstance())
                .isSameAs(TransactionIdGenerator.getInstance());
    }

    @Test
    void idsAreUniqueUnderConcurrency() {
        Set<String> ids = ConcurrentHashMap.newKeySet();
        try (ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int t = 0; t < 500; t++) {
                pool.submit(() -> {
                    for (int i = 0; i < 20; i++) {
                        ids.add(TransactionIdGenerator.getInstance().nextId());
                    }
                });
            }
        }
        assertThat(ids).hasSize(10_000);
        assertThat(ids).allMatch(id -> id.matches("TXN-BLR-\\d{7}"));
    }
}
