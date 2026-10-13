package com.handbook.structural.bridge.streaming;

/** Implementor: the low-level operations every device supports. */
public interface Device {

    String name();

    boolean isOn();

    void setPower(boolean on);

    int volume();

    void setVolume(int volume);

    void open(String content);
}
