package com.handbook.principles.solid.dip.after;

/** Low-level detail now depends on the abstraction (the dependency arrow is inverted). */
public class EmailSender implements MessageSender {
    public String send(String to, String text) { return "EMAIL to " + to + ": " + text; }
}
