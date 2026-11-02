package com.handbook.principles.solid.isp.after;

/** Implements only what it can actually do. No UnsupportedOperationException anywhere. */
public class BasicPrinter implements Printer {
    public String print(String document) { return "printed " + document; }
}
