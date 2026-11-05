package com.handbook.structural.adapter.healthcare;

import java.util.HashMap;
import java.util.Map;

public class PulseOximeterAdapter implements VitalsSensor {

    private final LegacyPulseOximeter device;

    public PulseOximeterAdapter(LegacyPulseOximeter device) {
        this.device = device;
    }

    @Override
    public Reading read() {
        String frame = device.readFrame();
        if (!frame.startsWith("$OX,") || !frame.endsWith("*")) {
            throw new IllegalStateException("Corrupt frame: " + frame); // (1)!
        }
        Map<String, String> fields = new HashMap<>();
        for (String part : frame.substring(4, frame.length() - 1).split(",")) {
            String[] kv = part.split("=");
            fields.put(kv[0], kv[1]);
        }
        return new Reading(fields.get("ID"), "SpO2", Double.parseDouble(fields.get("SPO2")), "%");
    }
}
