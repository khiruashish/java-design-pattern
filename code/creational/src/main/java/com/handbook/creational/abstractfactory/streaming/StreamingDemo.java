package com.handbook.creational.abstractfactory.streaming;

public class StreamingDemo {

    public static void main(String[] args) {
        System.out.println("=== Streaming: Device-Specific Playback ===");
        for (DeviceType device : DeviceType.values()) {
            PlaybackScreen screen = new PlaybackScreen(DeviceUiFactory.forDevice(device));
            System.out.println(device + ":");
            System.out.println("    " + screen.render("The Monsoon Express"));
        }
    }
}
