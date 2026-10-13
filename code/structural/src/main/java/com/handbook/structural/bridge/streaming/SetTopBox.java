package com.handbook.structural.bridge.streaming;

public class SetTopBox implements Device {

    private boolean on;
    private int volume = 8;

    public String name() { return "SetTopBox"; }
    public boolean isOn() { return on; }
    public void setPower(boolean on) { this.on = on; }
    public int volume() { return volume; }
    public void setVolume(int v) { this.volume = Math.clamp(v, 0, 30); } // hardware max 30

    public void open(String content) {
        System.out.println("    [SetTopBox] tuning to channel for '" + content + "'");
    }
}
