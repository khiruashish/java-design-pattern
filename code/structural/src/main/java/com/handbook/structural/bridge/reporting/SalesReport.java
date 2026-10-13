package com.handbook.structural.bridge.reporting;

import java.util.List;

public class SalesReport extends Report {

    public SalesReport(ReportRenderer renderer) {
        super(renderer);
    }

    protected String title() { return "Q3 Sales by Region"; }

    protected List<String> columns() { return List.of("Region", "Revenue (INR lakh)"); }

    protected List<List<String>> rows() {
        return List.of(List.of("North", "182.4"), List.of("South", "211.9"));
    }
}
