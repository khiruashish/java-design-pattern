package com.handbook.behavioral.mediator.chat;

public class ChatDemo {

    public static void main(String[] args) {
        System.out.println("=== SaaS: #incident-payments Channel ===");
        ChatRoom room = new ChatRoom("incident-payments");
        ChatRoom.Member priya = room.join("priya");
        ChatRoom.Member arjun = room.join("arjun");
        ChatRoom.Member bot = room.join("alertbot");

        bot.say("p99 latency on /charge is 4.2s");
        arjun.say("@priya can you check the gateway dashboard?");
        priya.say("on it, looks like the bank is degraded");
        room.mute("alertbot");
        bot.say("p99 latency on /charge is 4.3s");
    }
}
