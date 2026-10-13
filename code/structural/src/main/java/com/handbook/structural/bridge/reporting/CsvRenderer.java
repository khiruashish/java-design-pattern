package com.handbook.structural.bridge.reporting;

import java.util.List;
import java.util.stream.Collectors;

public class CsvRenderer implements ReportRenderer {

    @Override
    public String render(String title, List<String> columns, List<List<String>> rows) {
        StringBuilder sb = new StringBuilder("# ").append(title).append('\n');
        sb.append(String.join(",", columns)).append('\n');
        rows.forEach(r -> sb.append(r.stream().map(CsvRenderer::escape).collect(Collectors.joining(","))).append('\n'));
        return sb.toString().stripTrailing();
    }

    private static String escape(String v) {
        return v.contains(",") ? '"' + v + '"' : v;
    }
}
