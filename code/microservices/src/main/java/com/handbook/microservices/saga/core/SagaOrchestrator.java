package com.handbook.microservices.saga.core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * An orchestration-based saga: run local transactions in order; if one fails, run the
 * compensations of the completed steps in reverse order. Steps after the pivot step are
 * retried until they succeed instead of being compensated.
 */
public final class SagaOrchestrator {

    /** A step's action and compensation each call one service's local transaction. */
    public record Step(String name, Runnable action, Runnable compensation, Kind kind) {

        public enum Kind { COMPENSATABLE, PIVOT, RETRIABLE }

        public static Step compensatable(String name, Runnable action, Runnable compensation) {
            return new Step(name, action, compensation, Kind.COMPENSATABLE);
        }

        public static Step pivot(String name, Runnable action) {
            return new Step(name, action, () -> { }, Kind.PIVOT);
        }

        public static Step retriable(String name, Runnable action) {
            return new Step(name, action, () -> { }, Kind.RETRIABLE);
        }
    }

    public enum Outcome { COMPLETED, COMPENSATED }

    private final List<String> log = new ArrayList<>();
    private final int maxRetries;

    public SagaOrchestrator(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public Outcome run(String sagaId, List<Step> steps) {
        Deque<Step> done = new ArrayDeque<>();
        for (Step step : steps) {
            if (step.kind() == Step.Kind.RETRIABLE) {
                runWithRetries(sagaId, step); // (1)!
                continue;
            }
            try {
                step.action().run();
                record(sagaId, step.name() + " ✔");
                done.push(step); // (2)!
            } catch (RuntimeException e) {
                record(sagaId, step.name() + " ✘ " + e.getMessage());
                compensate(sagaId, done);
                return Outcome.COMPENSATED;
            }
        }
        record(sagaId, "COMPLETED");
        return Outcome.COMPLETED;
    }

    private void compensate(String sagaId, Deque<Step> done) {
        while (!done.isEmpty()) { // (3)!
            Step step = done.pop();
            step.compensation().run();
            record(sagaId, "undo " + step.name() + " ✔");
        }
        record(sagaId, "COMPENSATED");
    }

    private void runWithRetries(String sagaId, Step step) {
        for (int attempt = 1; ; attempt++) {
            try {
                step.action().run();
                record(sagaId, step.name() + " ✔" + (attempt > 1 ? " (attempt " + attempt + ")" : ""));
                return;
            } catch (RuntimeException e) {
                record(sagaId, step.name() + " ✘ " + e.getMessage() + ", will retry");
                if (attempt == maxRetries) {
                    record(sagaId, step.name() + " needs manual attention"); // (4)!
                    return;
                }
            }
        }
    }

    private void record(String sagaId, String entry) {
        log.add(entry);
        System.out.println("  [" + sagaId + "] " + entry);
    }

    public List<String> log() {
        return List.copyOf(log);
    }
}
