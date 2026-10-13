package com.handbook.creational.prototype.healthcare;

import java.util.ArrayList;
import java.util.List;

/** A lab report. Templates are configured once and copied for each patient. */
public class LabReport {

    public static final class TestResult {
        private final String test;
        private final String referenceRange;
        private String value = "pending";

        public TestResult(String test, String referenceRange) {
            this.test = test;
            this.referenceRange = referenceRange;
        }

        TestResult(TestResult other) { // copy constructor
            this(other.test, other.referenceRange);
            this.value = other.value;
        }

        @Override
        public String toString() {
            return "%s=%s (ref %s)".formatted(test, value, referenceRange);
        }
    }

    private final String panel;
    private final List<TestResult> results;
    private String patientId = "-";

    public LabReport(String panel, List<TestResult> results) {
        this.panel = panel;
        this.results = new ArrayList<>(results);
    }

    private LabReport(LabReport source) { // (1)!
        this.panel = source.panel;
        this.patientId = source.patientId;
        this.results = new ArrayList<>();
        for (TestResult r : source.results) {
            this.results.add(new TestResult(r)); // (2)!
        }
    }

    public LabReport copy() { // (3)!
        return new LabReport(this);
    }

    public LabReport forPatient(String patientId) {
        this.patientId = patientId;
        return this;
    }

    public LabReport record(String test, String value) {
        results.stream().filter(r -> r.test.equals(test)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Test not in panel: " + test))
                .value = value;
        return this;
    }

    @Override
    public String toString() {
        return "%s [%s] %s".formatted(panel, patientId, results);
    }
}
