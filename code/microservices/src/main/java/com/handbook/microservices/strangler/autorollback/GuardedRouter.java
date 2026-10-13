package com.handbook.microservices.strangler.autorollback;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;

/**
 * Coding challenge: route traffic to the new service, but watch its error rate over the last N
 * calls. If it exceeds the threshold, switch back to legacy automatically and stay there until
 * a human re-enables the new path.
 */
public final class GuardedRouter {

    private final int window;
    private final int maxErrorPercent;
    private final Deque<Boolean> recent = new ArrayDeque<>();
    private boolean rolledBack;

    public GuardedRouter(int window, int maxErrorPercent) {
        this.window = window;
        this.maxErrorPercent = maxErrorPercent;
    }

    public synchronized String route(Supplier<String> modern, Supplier<String> legacy) {
        if (rolledBack) {
            return legacy.get();
        }
        try {
            String result = modern.get();
            record(false);
            return result;
        } catch (RuntimeException e) {
            record(true);
            return legacy.get(); // the user still gets an answer
        }
    }

    private void record(boolean error) {
        recent.addLast(error);
        if (recent.size() > window) {
            recent.removeFirst();
        }
        long errors = recent.stream().filter(b -> b).count();
        if (recent.size() == window && errors * 100 / window > maxErrorPercent) {
            rolledBack = true;
            System.out.println("    AUTO-ROLLBACK: " + errors + " errors in last " + window + " calls");
        }
    }

    public synchronized void reEnable() {
        rolledBack = false;
        recent.clear();
    }

    public synchronized boolean rolledBack() {
        return rolledBack;
    }
}
