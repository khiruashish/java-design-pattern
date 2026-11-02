package com.handbook.principles.solid.dip.after;

/** The abstraction is owned by the high-level module and expressed in its terms. */
public interface MessageSender {
    String send(String to, String text);
}
