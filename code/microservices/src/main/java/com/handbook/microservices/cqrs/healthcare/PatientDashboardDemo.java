package com.handbook.microservices.cqrs.healthcare;

import com.handbook.microservices.cqrs.healthcare.PatientDashboardView.AppointmentBooked;
import com.handbook.microservices.cqrs.healthcare.PatientDashboardView.LabResultReady;
import com.handbook.microservices.cqrs.healthcare.PatientDashboardView.PrescriptionEnded;
import com.handbook.microservices.cqrs.healthcare.PatientDashboardView.PrescriptionIssued;

public class PatientDashboardDemo {

    public static void main(String[] args) {
        var view = new PatientDashboardView();

        System.out.println("=== Healthcare: doctor's dashboard built from three services' events ===");
        view.on(new PrescriptionIssued("PAT-5", "Metformin 500 mg"));        // pharmacy service
        view.on(new PrescriptionIssued("PAT-5", "Amoxicillin 250 mg"));      // pharmacy service
        view.on(new LabResultReady("PAT-5", "HbA1c %", 7.9, 6.5));           // lab service
        view.on(new LabResultReady("PAT-5", "TSH mIU/L", 2.1, 4.5));         // lab service (normal)
        view.on(new AppointmentBooked("PAT-5", "2026-10-20 10:30", "Dr. Iyer")); // appointments service
        view.on(new PrescriptionEnded("PAT-5", "Amoxicillin 250 mg"));       // course finished

        var d = view.dashboard("PAT-5");
        System.out.println("Next appointment  : " + d.nextAppointment());
        System.out.println("Abnormal labs     : " + d.abnormalLabs());
        System.out.println("Active medication : " + d.activeMedications());
        System.out.println("Unknown patient   : " + view.dashboard("PAT-404"));
    }
}
