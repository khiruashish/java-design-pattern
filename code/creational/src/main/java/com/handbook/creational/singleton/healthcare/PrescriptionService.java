package com.handbook.creational.singleton.healthcare;

public class PrescriptionService {

    public void prescribe(String user, String patientId) {
        AuditLogger.getInstance().record(user, "PRESCRIBE", patientId);
    }
}
