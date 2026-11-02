package com.handbook.principles.solid.isp.after;

/** A device that really does everything simply implements all the small interfaces. */
public class MultiFunctionDevice implements Printer, Scanner, Fax {
    public String print(String document) { return "printed " + document; }

    public String scan(String document) { return "scanned " + document; }

    public String fax(String document, String number) { return "faxed " + document + " to " + number; }
}
