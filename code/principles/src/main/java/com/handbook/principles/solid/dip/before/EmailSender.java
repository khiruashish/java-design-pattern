package com.handbook.principles.solid.dip.before;

/** A low-level detail: how a message is physically sent. */
public class EmailSender {
    public String send(String to, String text) {
        return "EMAIL to " + to + ": " + text;
    }
}
