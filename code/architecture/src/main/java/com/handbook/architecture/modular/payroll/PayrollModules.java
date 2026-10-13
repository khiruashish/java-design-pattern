package com.handbook.architecture.modular.payroll;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * HR and Payroll modules communicate only through an EventPublisher port. Today it's in-process;
 * when payroll is extracted into its own service, only the publisher implementation changes.
 */
public final class PayrollModules {

    private PayrollModules() {
    }

    public record EmployeeHired(String employeeId, String name, long monthlySalaryPaise) {
    }

    public interface EventPublisher {
        void publish(EmployeeHired event);
    }

    /** Wiring today: a direct in-process call to the subscriber. */
    public static EventPublisher inProcess(Consumer<EmployeeHired> subscriber) {
        return subscriber::accept;
    }

    /** Wiring after extraction: events go to an outbox table and Kafka (here: a list). */
    public static EventPublisher outbox(List<String> outboxTable) {
        return e -> outboxTable.add("topic=hr.employee-hired key=" + e.employeeId() + " payload=" + e);
    }

    /** HR module: hires people and announces it. Unchanged across both wirings. */
    public static final class HrModule {
        private final EventPublisher publisher;

        public HrModule(EventPublisher publisher) {
            this.publisher = publisher;
        }

        public void hire(String id, String name, long monthlySalaryPaise) {
            // ...HR's own records, contracts, onboarding tasks...
            publisher.publish(new EmployeeHired(id, name, monthlySalaryPaise)); // (1)!
        }
    }

    /** Payroll module: keeps its own copy of what it needs. */
    public static final class PayrollModule {
        private final List<String> payees = new ArrayList<>();

        public void on(EmployeeHired e) {
            payees.add(e.employeeId() + " " + String.format("₹%,d/month", e.monthlySalaryPaise() / 100));
        }

        public List<String> payees() {
            return List.copyOf(payees);
        }
    }
}
