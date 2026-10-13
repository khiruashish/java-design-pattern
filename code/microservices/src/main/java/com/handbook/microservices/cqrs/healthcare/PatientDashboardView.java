package com.handbook.microservices.cqrs.healthcare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A read model fed by events from three services (appointments, lab, pharmacy). The doctor's
 * dashboard becomes one key lookup instead of three remote calls and an in-memory join.
 */
public final class PatientDashboardView {

    public sealed interface ClinicalEvent permits AppointmentBooked, LabResultReady, PrescriptionIssued, PrescriptionEnded {
        String patientId();
    }

    public record AppointmentBooked(String patientId, String when, String doctor) implements ClinicalEvent {
    }

    public record LabResultReady(String patientId, String test, double value, double upperLimit) implements ClinicalEvent {
    }

    public record PrescriptionIssued(String patientId, String drug) implements ClinicalEvent {
    }

    public record PrescriptionEnded(String patientId, String drug) implements ClinicalEvent {
    }

    public record Dashboard(String nextAppointment, List<String> abnormalLabs, List<String> activeMedications) {
    }

    private static final class Row {
        String nextAppointment = "none";
        final List<String> abnormalLabs = new ArrayList<>();
        final List<String> meds = new ArrayList<>();
    }

    private final Map<String, Row> rows = new HashMap<>();

    public void on(ClinicalEvent event) {
        Row row = rows.computeIfAbsent(event.patientId(), k -> new Row());
        switch (event) {
            case AppointmentBooked a -> row.nextAppointment = a.when() + " with " + a.doctor();
            case LabResultReady l -> {
                if (l.value() > l.upperLimit()) { // (1)!
                    row.abnormalLabs.add(l.test() + " " + l.value() + " (max " + l.upperLimit() + ")");
                }
            }
            case PrescriptionIssued p -> row.meds.add(p.drug());
            case PrescriptionEnded p -> row.meds.remove(p.drug());
        }
    }

    public Dashboard dashboard(String patientId) { // (2)!
        Row r = rows.getOrDefault(patientId, new Row());
        return new Dashboard(r.nextAppointment, List.copyOf(r.abnormalLabs), List.copyOf(r.meds));
    }
}
