package com.handbook.creational.singleton.healthcare;

public class HealthcareDemo {

    public static void main(String[] args) {
        System.out.println("=== Hospital: Patient Record Audit Trail ===");
        PatientRecordService records = new PatientRecordService();
        PrescriptionService prescriptions = new PrescriptionService();

        records.view("dr.mehta", "P-1001");
        records.updateVitals("nurse.anna", "P-1001");
        prescriptions.prescribe("dr.mehta", "P-1001");
        records.view("dr.khan", "P-2002");

        System.out.println("Audit trail:");
        AuditLogger.getInstance().entries().forEach(e -> System.out.println("  " + e));
        System.out.println("Single audit store? "
                + (AuditLogger.getInstance() == AuditLogger.getInstance()));
    }
}
