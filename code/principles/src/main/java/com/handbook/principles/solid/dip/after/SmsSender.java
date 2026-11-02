package com.handbook.principles.solid.dip.after;

public class SmsSender implements MessageSender {
    public String send(String to, String text) { return "SMS to " + to + ": " + text; }
}
