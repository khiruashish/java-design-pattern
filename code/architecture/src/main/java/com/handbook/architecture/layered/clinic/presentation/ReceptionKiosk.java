package com.handbook.architecture.layered.clinic.presentation;

import com.handbook.architecture.layered.clinic.service.BookingService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/** Reception kiosk: staff type "10:30" for today's date. Different input, same rules. */
public class ReceptionKiosk {

    private final BookingService service;
    private final LocalDate today;

    public ReceptionKiosk(BookingService service, LocalDate today) {
        this.service = service;
        this.today = today;
    }

    public String enter(String patient, String doctor, String hhmm) {
        return "KIOSK> " + service.book(patient, doctor, LocalDateTime.of(today, LocalTime.parse(hhmm)));
    }
}
