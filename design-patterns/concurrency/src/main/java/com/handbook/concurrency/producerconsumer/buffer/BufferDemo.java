package com.handbook.concurrency.producerconsumer.buffer;

import java.util.concurrent.atomic.AtomicLong;

public class BufferDemo {

    public static void main(String[] args) throws InterruptedException {
        BoundedBuffer<Integer> buffer = new BoundedBuffer<>(4);
        AtomicLong sum = new AtomicLong();

        Thread consumer = Thread.ofPlatform().start(() -> {
            try {
                for (int i = 0; i < 1_000; i++) {
                    sum.addAndGet(buffer.take());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        for (int i = 1; i <= 1_000; i++) {
            buffer.put(i);
        }
        consumer.join();
        System.out.println("Sum of 1..1000 received: " + sum.get() + " (expected 500500)");
        System.out.println("Buffer empty at the end: " + (buffer.size() == 0));
    }
}
