package com.handbook.behavioral.template.importer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Template: the import workflow is fixed; formats fill in the steps. */
public abstract class DataImporter {

    public final String run(List<String> lines) { // (1)!
        List<Map<String, String>> accepted = new ArrayList<>();
        int rejected = 0;
        List<String> data = skipFirstLine() ? lines.subList(1, lines.size()) : lines; // (2)!
        for (String line : data) {
            Map<String, String> record = parse(line);           // abstract step
            Optional<String> problem = validate(record);        // abstract step
            if (problem.isPresent()) {
                rejected++;
                onRejected(line, problem.get());                // hook with default behavior
            } else {
                accepted.add(record);
            }
        }
        load(accepted);                                          // shared step
        return "%s: %d loaded, %d rejected".formatted(name(), accepted.size(), rejected);
    }

    protected abstract String name();

    protected abstract Map<String, String> parse(String line);

    protected abstract Optional<String> validate(Map<String, String> record);

    protected boolean skipFirstLine() { // (3)!
        return false;
    }

    protected void onRejected(String line, String problem) {
        System.out.println("  rejected [" + line.strip() + "]: " + problem);
    }

    private void load(List<Map<String, String>> records) {
        records.forEach(r -> System.out.println("  load " + r));
    }
}
