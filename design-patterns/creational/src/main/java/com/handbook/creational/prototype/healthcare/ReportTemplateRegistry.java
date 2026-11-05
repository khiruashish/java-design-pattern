package com.handbook.creational.prototype.healthcare;

import com.handbook.creational.prototype.healthcare.LabReport.TestResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Prototype registry: configured templates by name; callers always get a copy. */
public class ReportTemplateRegistry {

    private final Map<String, LabReport> templates = new HashMap<>();

    public ReportTemplateRegistry() {
        // Imagine these coming from a slow master-data service; it happens once.
        templates.put("CBC", new LabReport("Complete Blood Count", List.of(
                new TestResult("Hemoglobin", "13.5-17.5 g/dL"),
                new TestResult("WBC", "4.5-11.0 x10^9/L"),
                new TestResult("Platelets", "150-450 x10^9/L"))));
        templates.put("LIPID", new LabReport("Lipid Profile", List.of(
                new TestResult("LDL", "<100 mg/dL"),
                new TestResult("HDL", ">40 mg/dL"))));
    }

    public LabReport newReport(String code) {
        LabReport template = templates.get(code);
        if (template == null) {
            throw new IllegalArgumentException("Unknown panel " + code);
        }
        return template.copy(); // (1)!
    }

    public String template(String code) {
        return templates.get(code).toString();
    }
}
