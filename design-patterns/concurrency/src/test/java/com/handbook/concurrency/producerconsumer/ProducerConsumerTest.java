package com.handbook.concurrency.producerconsumer;

import com.handbook.concurrency.producerconsumer.buffer.BoundedBuffer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

class ProducerConsumerTest {

    @Test
    @Timeout(10)
    void manyProducersAndConsumersLoseNothing() throws Exception {
        BoundedBuffer<Integer> buffer = new BoundedBuffer<>(8);
        Set<Integer> received = ConcurrentHashMap.newKeySet();
        try (ExecutorService ex = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int c = 0; c < 4; c++) {
                ex.submit(() -> {
                    for (int i = 0; i < 2_500; i++) {
                        received.add(buffer.take());
                    }
                    return null;
                });
            }
            for (int p = 0; p < 4; p++) {
                int base = p * 2_500;
                ex.submit(() -> {
                    for (int i = 0; i < 2_500; i++) {
                        buffer.put(base + i);
                    }
                    return null;
                });
            }
        }
        assertThat(received).hasSize(10_000);
    }
}
