package com.handbook.structural.bridge.reporting;

import java.util.List;

public class HtmlRenderer implements ReportRenderer {

    @Override
    public String render(String title, List<String> columns, List<List<String>> rows) {
        StringBuilder sb = new StringBuilder("<h2>").append(title).append("</h2>\n<table>\n  <tr>");
        columns.forEach(c -> sb.append("<th>").append(c).append("</th>"));
        sb.append("</tr>\n");
        for (List<String> r : rows) {
            sb.append("  <tr>");
            r.forEach(v -> sb.append("<td>").append(v).append("</td>"));
            sb.append("</tr>\n");
        }
        return sb.append("</table>").toString();
    }
}
