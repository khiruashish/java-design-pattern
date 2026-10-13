package com.handbook.structural.adapter.healthcare;

/** Target: the ICU dashboard reads every device through this interface. */
public interface VitalsSensor {

    record Reading(String deviceId, String metric, double value, String unit) {
        @Override
        public String toString() {
            return "%-8s %-11s %6.1f %s".formatted(deviceId, metric, value, unit);
        }
    }

    Reading read();
}
