package com.handbook.structural.adapter.healthcare;

/** Adaptee: old device that emits a raw serial-protocol frame. */
public class LegacyPulseOximeter {

    public String readFrame() {
        return "$OX,ID=OX-7,SPO2=94,PR=88*";
    }
}
