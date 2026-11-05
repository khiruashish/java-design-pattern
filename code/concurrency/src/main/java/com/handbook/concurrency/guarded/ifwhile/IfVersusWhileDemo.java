package com.handbook.concurrency.guarded.ifwhile;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

/** Coding challenge: why the guard must be re-checked in a WHILE loop, not an IF. */
public class IfVersusWhileDemo {

    static final class Mailbox {
        private final Queue<String> items = new ArrayDeque<>();
        private final boolean useWhile;

        Mailbox(boolean useWhile) {
            this.useWhile = useWhile;
        }

        synchronized void put(String item) {
            items.add(item);
            notifyAll();
        }

        synchronized String take() throws InterruptedException {
            if (useWhile) {
                while (items.isEmpty()) {
                    wait();
                }
            } else {
                if (items.isEmpty()) {
                    wait(); // BUG: after waking, another consumer may already have taken the item
                }
            }
            return items.poll();
        }
    }

    static List<String> run(boolean useWhile) throws InterruptedException {
        Mailbox box = new Mailbox(useWhile);
        List<String> got = Collections.synchronizedList(new ArrayList<>());
        Thread a = Thread.ofPlatform().start(() -> take(box, got));
        Thread b = Thread.ofPlatform().start(() -> take(box, got));
        while (a.getState() != Thread.State.WAITING || b.getState() != Thread.State.WAITING) {
            Thread.onSpinWait(); // make sure both consumers are waiting
        }
        box.put("job-1"); // ONE item, notifyAll wakes BOTH consumers
        Thread.sleep(100);
        if (useWhile) {
            box.put("job-2"); // the second consumer is still (correctly) waiting
        }
        a.join();
        b.join();
        List<String> sorted = new ArrayList<>(got);
        sorted.sort((x, y) -> String.valueOf(x).compareTo(String.valueOf(y)));
        return sorted;
    }

    static void take(Mailbox box, List<String> got) {
        try {
            got.add(String.valueOf(box.take()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Challenge: if (guard) wait() vs while (guard) wait() ===");
        System.out.println("if   : consumers got " + run(false) + "  <- one woke up to an empty queue");
        System.out.println("while: consumers got " + run(true) + "  <- the guard was re-checked after waking");
    }
}
