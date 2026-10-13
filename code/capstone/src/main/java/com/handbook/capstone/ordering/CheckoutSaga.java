package com.handbook.capstone.ordering;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;

/**
 * [Saga] (orchestration) for checkout: each step has a compensation; if a later step fails, the
 * completed steps are undone in reverse order. Every action is logged so the demo shows the flow.
 */
public final class CheckoutSaga {

    public record Step(String name, Runnable action, Runnable compensation) {
    }

    public static final class Failed extends RuntimeException {
        Failed(String message) {
            super(message);
        }
    }

    private final Consumer<String> log;

    public CheckoutSaga(Consumer<String> log) {
        this.log = log;
    }

    public void run(List<Step> steps) {
        Deque<Step> done = new ArrayDeque<>();
        for (Step step : steps) {
            try {
                step.action().run();
                log.accept(step.name() + " ✔");
                done.push(step);
            } catch (RuntimeException e) {
                log.accept(step.name() + " ✘ " + e.getMessage());
                while (!done.isEmpty()) {
                    Step undo = done.pop();
                    undo.compensation().run();
                    log.accept("compensate: undo " + undo.name());
                }
                throw new Failed(step.name() + " failed: " + e.getMessage());
            }
        }
    }
}
