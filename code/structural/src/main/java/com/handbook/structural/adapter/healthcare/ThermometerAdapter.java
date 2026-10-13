package com.handbook.structural.adapter.healthcare;

public class ThermometerAdapter implements VitalsSensor {

    private final VendorThermometer device;

    public ThermometerAdapter(VendorThermometer device) {
        this.device = device;
    }

    @Override
    public Reading read() {
        double fahrenheit = device.getTempF10() / 10.0;
        double celsius = Math.round((fahrenheit - 32) * 5 / 9 * 10) / 10.0; // (1)!
        return new Reading(device.serial(), "temperature", celsius, "°C");
    }
}
