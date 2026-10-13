package com.handbook.architecture.layered.clinic;

import com.handbook.architecture.layered.clinic.data.AppointmentDao;
import com.handbook.architecture.layered.clinic.presentation.ReceptionKiosk;
import com.handbook.architecture.layered.clinic.presentation.WebBookingPage;
import com.handbook.architecture.layered.clinic.service.BookingService;

import java.time.LocalDate;

public class ClinicBookingDemo {

    public static void main(String[] args) {
        BookingService service = new BookingService(new AppointmentDao());
        WebBookingPage web = new WebBookingPage(service);
        ReceptionKiosk kiosk = new ReceptionKiosk(service, LocalDate.of(2026, 10, 14));

        System.out.println("=== Clinic: two front ends, one service layer with the rules ===");
        System.out.println(web.submit("PAT-1", "Dr. Iyer", "2026-10-14T10:30"));
        System.out.println(kiosk.enter("PAT-2", "Dr. Iyer", "10:30"));
        System.out.println(kiosk.enter("PAT-2", "Dr. Iyer", "11:00"));
        System.out.println(web.submit("PAT-2", "Dr. Rao", "2026-10-14T12:00"));
        System.out.println(web.submit("PAT-2", "Dr. Rao", "2026-10-14T15:00"));
        System.out.println(kiosk.enter("PAT-3", "Dr. Rao", "18:15"));
    }
}
