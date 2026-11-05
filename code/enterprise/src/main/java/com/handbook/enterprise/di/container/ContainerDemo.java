package com.handbook.enterprise.di.container;

import com.handbook.enterprise.di.container.Clinic.AppointmentService;
import com.handbook.enterprise.di.container.Clinic.BillingService;
import com.handbook.enterprise.di.container.Clinic.EmrDirectory;
import com.handbook.enterprise.di.container.Clinic.PatientDirectory;
import com.handbook.enterprise.di.container.Clinic.ReminderChannel;
import com.handbook.enterprise.di.container.Clinic.WhatsAppChannel;

public class ContainerDemo {

    public static void main(String[] args) {
        System.out.println("=== Healthcare: A Tiny IoC Container Autowiring Constructors ===");
        MiniContainer container = new MiniContainer()
                .bind(PatientDirectory.class, EmrDirectory.class)
                .bind(ReminderChannel.class, WhatsAppChannel.class);

        AppointmentService service = container.get(AppointmentService.class);
        System.out.println(service.sendReminder("P-9", "Tue 10:30, Dr. Menon"));
        System.out.println("Same singleton on second lookup: " + (service == container.get(AppointmentService.class)));

        try {
            new MiniContainer().get(AppointmentService.class);
        } catch (IllegalStateException e) {
            System.out.println("Missing binding  -> " + e.getMessage());
        }
        try {
            container.get(BillingService.class);
        } catch (IllegalStateException e) {
            System.out.println("Cycle at startup -> " + e.getMessage());
        }
    }
}
