package com.handbook.principles.other.telldontask;

public class TellDontAskDemo {
    public static void main(String[] args) {
        // ASK: the caller duplicates the rule (and here forgets the hysteresis)
        Thermostat asked = new Thermostat(21);
        int reading = 20;
        if (reading < asked.getTargetCelsius()) asked.setHeating(true);
        System.out.println("ask : reading " + reading + " -> heating " + asked.isHeating() + "  (rule re-implemented by the caller)");

        // TELL: the thermostat applies its own rule
        Thermostat told = new Thermostat(21);
        told.onTemperatureReading(reading);
        System.out.println("tell: reading " + reading + " -> heating " + told.isHeating() + " (thermostat's own 1-degree hysteresis)");
        told.onTemperatureReading(18);
        System.out.println("tell: reading 18 -> heating " + told.isHeating());
    }
}
