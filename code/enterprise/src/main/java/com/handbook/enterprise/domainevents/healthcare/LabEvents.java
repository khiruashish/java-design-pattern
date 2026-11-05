package com.handbook.enterprise.domainevents.healthcare;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** Events as immutable facts in past tense, with when they happened, handled via pattern matching. */
public final class LabEvents {

    public sealed interface LabEvent permits ResultRecorded, ResultAmended {
        String patientId();
        Instant occurredAt();
    }

    public record ResultRecorded(String patientId, String test, double value, boolean critical, Instant occurredAt)
            implements LabEvent { }

    public record ResultAmended(String patientId, String test, double oldValue, double newValue, Instant occurredAt)
            implements LabEvent { }

    /** The aggregate: a lab order that knows the critical thresholds. */
    public static final class LabOrder {
        private final String patientId;
        private final Clock clock;
        private final List<LabEvent> events = new ArrayList<>();
        private Double potassium;

        public LabOrder(String patientId, Clock clock) {
            this.patientId = patientId;
            this.clock = clock;
        }

        public void recordPotassium(double mmolPerL) {
            boolean critical = mmolPerL < 2.5 || mmolPerL > 6.0; // (1)!
            if (potassium == null) {
                events.add(new ResultRecorded(patientId, "K+", mmolPerL, critical, clock.instant()));
            } else {
                events.add(new ResultAmended(patientId, "K+", potassium, mmolPerL, clock.instant()));
            }
            potassium = mmolPerL;
        }

        public List<LabEvent> pullEvents() {
            List<LabEvent> out = List.copyOf(events);
            events.clear();
            return out;
        }
    }

    /** Handler with an exhaustive switch: adding a new event type breaks compilation until handled. */
    public static String describe(LabEvent e) {
        return switch (e) { // (2)!
            case ResultRecorded r when r.critical() -> "PAGE on-call doctor: " + r.test() + "=" + r.value() + " for " + r.patientId();
            case ResultRecorded r -> "timeline: " + r.test() + "=" + r.value();
            case ResultAmended a -> "timeline: " + a.test() + " amended " + a.oldValue() + " -> " + a.newValue() + " (notify reviewer)";
        };
    }

    private LabEvents() {
    }
}
