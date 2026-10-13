package com.handbook.microservices.decomposition.healthcare;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Checks a proposed service split against the system's use cases: a use case that must WRITE
 * data owned by two services in one transaction is a warning sign (distributed transaction).
 */
public final class BoundaryChecker {

    public record UseCase(String name, Set<String> writes, Set<String> reads) {
    }

    private BoundaryChecker() {
    }

    public static Map<String, String> check(Map<String, String> tableOwner, List<UseCase> useCases) {
        Map<String, String> report = new LinkedHashMap<>();
        for (UseCase uc : useCases) {
            Set<String> writeOwners = new TreeSet<>();
            uc.writes().forEach(t -> writeOwners.add(tableOwner.get(t)));
            Set<String> readOwners = new TreeSet<>();
            uc.reads().forEach(t -> readOwners.add(tableOwner.get(t)));
            readOwners.removeAll(writeOwners);
            String verdict;
            if (writeOwners.size() > 1) {
                verdict = "WARN writes span " + writeOwners + " -> needs a saga or a different boundary"; // (1)!
            } else if (!readOwners.isEmpty()) {
                verdict = "ok   (reads from " + readOwners + ": API calls or a read model)";
            } else {
                verdict = "ok   (inside " + writeOwners + ")";
            }
            report.put(uc.name(), verdict);
        }
        return report;
    }
}
