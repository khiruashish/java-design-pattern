package com.handbook.structural.adapter.healthcare;

import java.util.List;

public class IcuDashboardDemo {

    public static void main(String[] args) {
        System.out.println("=== Hospital ICU: Bed 12 Vitals ===");
        List<VitalsSensor> bed12 = List.of(
                new ThermometerAdapter(new VendorThermometer()),
                new PulseOximeterAdapter(new LegacyPulseOximeter()));

        for (VitalsSensor sensor : bed12) {
            VitalsSensor.Reading r = sensor.read();
            boolean alert = (r.metric().equals("temperature") && r.value() >= 38.0)
                    || (r.metric().equals("SpO2") && r.value() < 95);
            System.out.println(r + (alert ? "   <-- ALERT" : ""));
        }
    }
}
