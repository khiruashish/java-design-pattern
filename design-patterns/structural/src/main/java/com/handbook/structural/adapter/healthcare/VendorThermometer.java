package com.handbook.structural.adapter.healthcare;

/** Adaptee: US vendor device, reports tenths of a degree Fahrenheit as an int. */
public class VendorThermometer {

    public int getTempF10() {
        return 1_013; // 101.3 °F
    }

    public String serial() {
        return "THM-22";
    }
}
