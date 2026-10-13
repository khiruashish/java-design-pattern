package com.handbook.structural.bridge.reporting;

public class ReportingDemo {

    public static void main(String[] args) {
        System.out.println("=== ERP Reporting: 2 reports x 2 formats = 4 outputs from 4 classes ===");
        ReportRenderer csv = new CsvRenderer();
        ReportRenderer html = new HtmlRenderer();

        System.out.println(new SalesReport(csv).generate());
        System.out.println();
        System.out.println(new SalesReport(html).generate());
        System.out.println();
        System.out.println(new InventoryReport(csv).generate());
        System.out.println();
        System.out.println(new InventoryReport(html).generate());
    }
}
