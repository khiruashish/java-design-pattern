package com.handbook.enterprise.domainevents.insurance;

import com.handbook.enterprise.domainevents.insurance.PolicyEvents.PolicyLapsed;
import com.handbook.enterprise.domainevents.insurance.PolicyEvents.PolicyLapsedV1;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

public class PolicyEventsDemo {

    public static void main(String[] args) {
        System.out.println("=== Insurance: Domain Event -> Integration Event -> Outbox ===");
        PolicyLapsed internal = new PolicyLapsed("POL-2207", "CUST-41", LocalDate.of(2026, 4, 1), 3,
                "Customer called twice about premium hike", 0.82);
        System.out.println("In-process handlers see : " + internal);
        System.out.println("  [renewals] schedule win-back call; [claims] block new claims on POL-2207");

        PolicyLapsedV1 published = PolicyEvents.toIntegrationEvent(internal);
        System.out.println("Other services receive  : " + published);
        Map<String, String> row = new TreeMap<>(PolicyEvents.outboxRow(published));
        System.out.println("Outbox row (same DB transaction as the lapse): " + row);
        System.out.println("Notes and risk score never leave the policy service.");
    }
}
