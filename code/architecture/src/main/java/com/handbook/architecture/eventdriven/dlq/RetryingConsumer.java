package com.handbook.architecture.eventdriven.dlq;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Coding challenge: a poison message must not block the partition forever. Retry a few times; if
 * it still fails, park it on a dead-letter queue with the error, and move on to the next message.
 */
public final class RetryingConsumer<E> {

    public record DeadLetter<E>(E event, String error, int attempts) {
    }

    private final Consumer<E> handler;
    private final int maxAttempts;
    private final List<DeadLetter<E>> deadLetters = new ArrayList<>();

    public RetryingConsumer(Consumer<E> handler, int maxAttempts) {
        this.handler = handler;
        this.maxAttempts = maxAttempts;
    }

    public void consume(List<E> partition) {
        for (E event : partition) {
            for (int attempt = 1; ; attempt++) {
                try {
                    handler.accept(event);
                    break;
                } catch (RuntimeException e) {
                    if (attempt == maxAttempts) {
                        deadLetters.add(new DeadLetter<>(event, e.getMessage(), attempt));
                        System.out.println("    -> DLQ after " + attempt + " attempts: " + event);
                        break;
                    }
                    System.out.println("    attempt " + attempt + " failed for " + event + ": " + e.getMessage());
                }
            }
        }
    }

    public List<DeadLetter<E>> deadLetters() {
        return List.copyOf(deadLetters);
    }
}
