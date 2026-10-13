package com.handbook.architecture.layered.clinic.presentation;

import com.handbook.architecture.layered.clinic.service.BookingService;

import java.time.LocalDateTime;

/** Web front end: parses the ISO date-time from the form and shows a green or red banner. */
public class WebBookingPage {

    private final BookingService service;

    public WebBookingPage(BookingService service) {
        this.service = service;
    }

    public String submit(String patient, String doctor, String isoSlot) {
        String result = service.book(patient, doctor, LocalDateTime.parse(isoSlot));
        return (result.startsWith("CONFIRMED") ? "[green] " : "[red] ") + result;
    }
}
