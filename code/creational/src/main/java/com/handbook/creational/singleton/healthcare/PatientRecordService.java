package com.handbook.creational.singleton.healthcare;

public class PatientRecordService {

    public void view(String user, String patientId) {
        AuditLogger.getInstance().record(user, "VIEW_RECORD", patientId);
    }

    public void updateVitals(String user, String patientId) {
        AuditLogger.getInstance().record(user, "UPDATE_VITALS", patientId);
    }
}
