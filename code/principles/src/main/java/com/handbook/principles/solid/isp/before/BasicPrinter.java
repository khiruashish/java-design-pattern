package com.handbook.principles.solid.isp.before;

/** A cheap printer is forced to "implement" scanning and faxing it can't do. */
public class BasicPrinter implements OfficeMachine {
    public String print(String document) { return "printed " + document; }

    public String scan(String document) { throw new UnsupportedOperationException("BasicPrinter cannot scan"); }

    public String fax(String document, String number) { throw new UnsupportedOperationException("BasicPrinter cannot fax"); }
}
