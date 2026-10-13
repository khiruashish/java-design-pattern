package com.handbook.structural.bridge.streaming;

public class SmartTv implements Device {

    private boolean on;
    private int volume = 20;

    public String name() { return "SmartTV"; }
    public boolean isOn() { return on; }
    public void setPower(boolean on) { this.on = on; }
    public int volume() { return volume; }
    public void setVolume(int v) { this.volume = Math.clamp(v, 0, 100); }

    public void open(String content) {
        System.out.println("    [SmartTV] launching app and streaming '" + content + "' in 4K");
    }
}
