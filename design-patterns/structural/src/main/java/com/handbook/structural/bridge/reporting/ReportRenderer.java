package com.handbook.structural.bridge.reporting;

import java.util.List;

/** Implementor: HOW a report is written out. */
public interface ReportRenderer {

    String render(String title, List<String> columns, List<List<String>> rows);
}
