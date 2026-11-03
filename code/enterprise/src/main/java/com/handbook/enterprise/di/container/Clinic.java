package com.handbook.enterprise.di.container;

/** Healthcare classes that know nothing about the container: plain constructors only. */
public final class Clinic {

    public interface PatientDirectory { String name(String patientId); }

    public interface ReminderChannel { String remind(String name, String when); }

    public static final class EmrDirectory implements PatientDirectory {
        public EmrDirectory() {
        }
        public String name(String patientId) { return patientId.equals("P-9") ? "Asha Rao" : "Unknown"; }
    }

    public static final class WhatsAppChannel implements ReminderChannel {
        public WhatsAppChannel() {
        }
        public String remind(String name, String when) { return "WhatsApp -> " + name + ": appointment " + when; }
    }

    public static final class AppointmentService {
        private final PatientDirectory directory;
        private final ReminderChannel channel;

        public AppointmentService(PatientDirectory directory, ReminderChannel channel) {
            this.directory = directory;
            this.channel = channel;
        }

        public String sendReminder(String patientId, String when) {
            return channel.remind(directory.name(patientId), when);
        }
    }

    // A deliberately broken pair: each needs the other.
    public static final class BillingService {
        public BillingService(InsuranceService insurance) {
        }
    }

    public static final class InsuranceService {
        public InsuranceService(BillingService billing) {
        }
    }

    private Clinic() {
    }
}
