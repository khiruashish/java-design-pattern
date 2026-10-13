package com.handbook.architecture.layered.clinic.data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Data layer: simple queries over stored appointments. */
public class AppointmentDao {

    public record AppointmentRow(String patient, String doctor, LocalDateTime slot) {
    }

    private final List<AppointmentRow> rows = new ArrayList<>();

    public boolean slotTaken(String doctor, LocalDateTime slot) {
        return rows.stream().anyMatch(r -> r.doctor().equals(doctor) && r.slot().equals(slot));
    }

    public long countForPatientOn(String patient, LocalDate day) {
        return rows.stream().filter(r -> r.patient().equals(patient) && r.slot().toLocalDate().equals(day)).count();
    }

    public void insert(AppointmentRow row) {
        rows.add(row);
    }
}
