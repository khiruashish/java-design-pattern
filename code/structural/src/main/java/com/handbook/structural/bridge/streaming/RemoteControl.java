package com.handbook.structural.bridge.streaming;

/** Abstraction: user-facing controls, built only from Device operations. */
public class RemoteControl {

    protected final Device device;

    public RemoteControl(Device device) {
        this.device = device;
    }

    public void togglePower() {
        device.setPower(!device.isOn());
        System.out.println("  " + device.name() + " power " + (device.isOn() ? "ON" : "OFF"));
    }

    public void volumeUp() {
        device.setVolume(device.volume() + 10);
        System.out.println("  " + device.name() + " volume " + device.volume());
    }
}
