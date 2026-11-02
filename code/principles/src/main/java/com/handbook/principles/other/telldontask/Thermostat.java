package com.handbook.principles.other.telldontask;

/** The object that owns the data also owns the decision. */
public class Thermostat {
    private final int targetCelsius;
    private boolean heating;

    public Thermostat(int targetCelsius) { this.targetCelsius = targetCelsius; }

    // ---- ASK style: getters let callers pull the data and make the decision outside
    public int getTargetCelsius() { return targetCelsius; }

    public void setHeating(boolean heating) { this.heating = heating; }

    public boolean isHeating() { return heating; }

    // ---- TELL style: give the object the new fact; it decides what that means
    public void onTemperatureReading(int currentCelsius) {
        heating = currentCelsius < targetCelsius - 1;     // 1 degree hysteresis, in ONE place
    }
}
