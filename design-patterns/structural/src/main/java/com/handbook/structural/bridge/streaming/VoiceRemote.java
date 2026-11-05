package com.handbook.structural.bridge.streaming;

/** Refined abstraction: adds voice commands, still works with ANY device. */
public class VoiceRemote extends RemoteControl {

    public VoiceRemote(Device device) {
        super(device);
    }

    public void say(String command) {
        System.out.println("  \"" + command + "\"");
        if (command.startsWith("play ")) {
            if (!device.isOn()) {
                togglePower();
            }
            device.open(command.substring(5));
        } else if (command.equals("louder")) {
            volumeUp();
            volumeUp();
        } else {
            System.out.println("  Sorry, I didn't understand that");
        }
    }
}
