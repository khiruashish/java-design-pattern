package com.handbook.enterprise.domainevents.healthcare;

import com.handbook.enterprise.domainevents.healthcare.LabEvents.LabOrder;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

public class LabEventsDemo {

    public static void main(String[] args) {
        System.out.println("=== Healthcare: Lab Results as Domain Events ===");
        Clock clock = Clock.fixed(Instant.parse("2026-02-10T03:15:00Z"), ZoneOffset.UTC);
        LabOrder ward = new LabOrder("PAT-17", clock);
        ward.recordPotassium(4.1);
        LabOrder icu = new LabOrder("PAT-88", clock);
        icu.recordPotassium(6.8);
        icu.recordPotassium(5.9); // re-run after haemolysed sample

        for (LabOrder order : new LabOrder[] {ward, icu}) {
            order.pullEvents().forEach(e -> System.out.println(e.occurredAt() + " " + LabEvents.describe(e)));
        }
    }
}
