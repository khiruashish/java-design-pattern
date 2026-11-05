package com.handbook.structural.bridge.reporting;

import java.util.List;

public class InventoryReport extends Report {

    public InventoryReport(ReportRenderer renderer) {
        super(renderer);
    }

    protected String title() { return "Low Stock Items"; }

    protected List<String> columns() { return List.of("SKU", "Name", "On hand"); }

    protected List<List<String>> rows() {
        return List.of(List.of("RICE-5KG", "Basmati Rice, 5kg", "12"));
    }
}
