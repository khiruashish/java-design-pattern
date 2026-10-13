package com.handbook.architecture.layered.clinic.service;

import com.handbook.architecture.layered.clinic.data.AppointmentDao;
import com.handbook.architecture.layered.clinic.data.AppointmentDao.AppointmentRow;

import java.time.LocalDateTime;

/**
 * The booking rules live here, ONCE. Every front end (web, reception desk, kiosk, phone bot)
 * calls this service, so the rules can't diverge between channels.
 */
public class BookingService {

    private final AppointmentDao dao;

    public BookingService(AppointmentDao dao) {
        this.dao = dao;
    }

    public String book(String patient, String doctor, LocalDateTime slot) {
        if (slot.getHour() < 9 || slot.getHour() >= 17) { // (1)!
            return "REJECTED: outside clinic hours";
        }
        if (dao.slotTaken(doctor, slot)) {
            return "REJECTED: " + doctor + " is already booked at " + slot.toLocalTime();
        }
        if (dao.countForPatientOn(patient, slot.toLocalDate()) >= 2) {
            return "REJECTED: max 2 appointments per patient per day";
        }
        dao.insert(new AppointmentRow(patient, doctor, slot));
        return "CONFIRMED: " + patient + " with " + doctor + " at " + slot.toLocalTime();
    }
}
