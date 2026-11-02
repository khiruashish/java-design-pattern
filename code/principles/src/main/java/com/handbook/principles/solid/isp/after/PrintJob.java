package com.handbook.principles.solid.isp.after;

import java.util.List;

/** A client that only prints depends only on Printer, so it accepts any device that can print. */
public class PrintJob {
    public String run(Printer printer, List<String> documents) {
        return String.join(", ", documents.stream().map(printer::print).toList());
    }
}
