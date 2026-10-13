package com.handbook.architecture.layered.reporting;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Closed vs. open layers for a read-only report. In strict (closed) layering every call passes
 * through the service layer, even when it adds nothing: the "architecture sinkhole".
 * Marking a query layer as OPEN lets the presentation layer read it directly.
 */
public final class StatementLayers {

    public record Txn(String date, String description, long paise) {
    }

    /** Data layer: a read-optimized query (in a real app, a SQL view or a reporting replica). */
    public static final class StatementQueries {
        private final Map<String, List<Txn>> data = Map.of("ACC-1", List.of(
                new Txn("2026-10-01", "Salary", 5_000_000),
                new Txn("2026-10-03", "Rent", -1_250_000),
                new Txn("2026-10-07", "Groceries", -89_900)));

        public List<Txn> forAccount(String account) {
            return data.getOrDefault(account, List.of());
        }
    }

    /** Service layer method that only forwards: no rules, no transaction, no transformation. */
    public static final class StatementService {
        private final StatementQueries queries;
        int passThroughCalls;

        public StatementService(StatementQueries queries) {
            this.queries = queries;
        }

        public List<Txn> statement(String account) {
            passThroughCalls++; // (1)!
            return queries.forAccount(account);
        }
    }

    public static String render(Supplier<List<Txn>> source) {
        StringBuilder sb = new StringBuilder();
        for (Txn t : source.get()) {
            sb.append(String.format("  %s %-10s %,12.2f%n", t.date(), t.description(), t.paise() / 100.0));
        }
        return sb.toString();
    }

    private StatementLayers() {
    }
}
