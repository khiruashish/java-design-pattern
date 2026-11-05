package com.handbook.structural.bridge.reporting;

import java.util.List;

/** Abstraction: WHAT the report contains. Holds a reference to the implementor (the bridge). */
public abstract class Report {

    protected final ReportRenderer renderer; // (1)!

    protected Report(ReportRenderer renderer) {
        this.renderer = renderer;
    }

    public final String generate() {
        return renderer.render(title(), columns(), rows()); // (2)!
    }

    protected abstract String title();

    protected abstract List<String> columns();

    protected abstract List<List<String>> rows();
}
