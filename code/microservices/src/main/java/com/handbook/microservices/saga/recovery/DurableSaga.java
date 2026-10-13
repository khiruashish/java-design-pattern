package com.handbook.microservices.saga.recovery;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Coding challenge: an orchestrator that survives a crash. Before and after each step it appends to
 * a durable saga log (a database table in real life). On restart, it reads the log: an unfinished
 * saga is rolled back by compensating every step that completed.
 */
public final class DurableSaga {

    public record Step(String name, Runnable action, Runnable compensation) {
    }

    /** Stands in for a saga_log table: saga id → ordered entries. */
    public static final class SagaLog {
        private final Map<String, List<String>> entries = new LinkedHashMap<>();

        void append(String sagaId, String entry) {
            entries.computeIfAbsent(sagaId, k -> new ArrayList<>()).add(entry);
        }

        public List<String> read(String sagaId) {
            return List.copyOf(entries.getOrDefault(sagaId, List.of()));
        }
    }

    private final SagaLog log;

    public DurableSaga(SagaLog log) {
        this.log = log;
    }

    public void run(String sagaId, List<Step> steps) {
        log.append(sagaId, "STARTED");
        for (Step step : steps) {
            log.append(sagaId, "BEGIN " + step.name());
            step.action().run();
            log.append(sagaId, "DONE " + step.name());
        }
        log.append(sagaId, "FINISHED");
    }

    /** Called on startup for every saga that has no FINISHED or ROLLED_BACK entry. */
    public void recover(String sagaId, List<Step> steps) {
        List<String> entries = log.read(sagaId);
        if (entries.contains("FINISHED") || entries.contains("ROLLED_BACK")) {
            return;
        }
        List<Step> completed = steps.stream().filter(s -> entries.contains("DONE " + s.name())).toList();
        for (int i = completed.size() - 1; i >= 0; i--) {
            Step step = completed.get(i);
            step.compensation().run(); // compensations must be idempotent: we may crash here too
            log.append(sagaId, "UNDONE " + step.name());
        }
        log.append(sagaId, "ROLLED_BACK");
    }
}
