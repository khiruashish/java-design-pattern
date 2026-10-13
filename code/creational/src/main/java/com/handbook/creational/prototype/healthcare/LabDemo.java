package com.handbook.creational.prototype.healthcare;

public class LabDemo {

    public static void main(String[] args) {
        System.out.println("=== Hospital Lab: Reports from Templates ===");
        ReportTemplateRegistry registry = new ReportTemplateRegistry();

        LabReport asha = registry.newReport("CBC").forPatient("P-1001")
                .record("Hemoglobin", "14.2").record("WBC", "6.1");
        LabReport vikram = registry.newReport("CBC").forPatient("P-1002")
                .record("Hemoglobin", "11.9");
        LabReport lipid = registry.newReport("LIPID").forPatient("P-1001")
                .record("LDL", "132");

        System.out.println(asha);
        System.out.println(vikram);
        System.out.println(lipid);
        System.out.println("Template untouched: " + registry.template("CBC"));
    }
}
