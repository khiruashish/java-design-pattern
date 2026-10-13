package com.handbook.structural.bridge.streaming;

public class RemoteDemo {

    public static void main(String[] args) {
        System.out.println("=== Streaming: Remotes x Devices ===");
        System.out.println("Basic remote + SmartTV:");
        RemoteControl basic = new RemoteControl(new SmartTv());
        basic.togglePower();
        basic.volumeUp();

        System.out.println("Voice remote + SetTopBox:");
        VoiceRemote voice = new VoiceRemote(new SetTopBox());
        voice.say("play Cricket World Cup Final");
        voice.say("louder");
        voice.say("louder");
        voice.say("make me a sandwich");
    }
}
