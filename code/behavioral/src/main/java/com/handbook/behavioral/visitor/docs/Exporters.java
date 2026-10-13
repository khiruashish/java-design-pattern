package com.handbook.behavioral.visitor.docs;

import com.handbook.behavioral.visitor.docs.Doc.*;

import java.util.stream.Collectors;

/** New operations = new visitors. The element classes never change. */
public final class Exporters {

    private Exporters() {
    }

    public static class Markdown implements Visitor<String> {
        public String heading(Heading h) { return "#".repeat(h.level()) + " " + h.text(); }
        public String paragraph(Paragraph p) { return p.text(); }
        public String image(Image i) { return "![" + i.alt() + "](" + i.url() + ")"; }
        public String table(Table t) {
            String head = "| " + String.join(" | ", t.header()) + " |";
            String sep = "|" + " --- |".repeat(t.header().size());
            String body = t.rows().stream().map(r -> "| " + String.join(" | ", r) + " |").collect(Collectors.joining("\n"));
            return head + "\n" + sep + "\n" + body;
        }
    }

    public static class Html implements Visitor<String> {
        public String heading(Heading h) { return "<h%d>%s</h%d>".formatted(h.level(), h.text(), h.level()); }
        public String paragraph(Paragraph p) { return "<p>" + p.text() + "</p>"; }
        public String image(Image i) { return "<img src=\"" + i.url() + "\" alt=\"" + i.alt() + "\">"; }
        public String table(Table t) { return "<table>(" + t.rows().size() + " rows)</table>"; }
    }

    public static class WordCount implements Visitor<Integer> {
        private static int words(String s) { return s.isBlank() ? 0 : s.trim().split("\\s+").length; }
        public Integer heading(Heading h) { return words(h.text()); }
        public Integer paragraph(Paragraph p) { return words(p.text()); }
        public Integer image(Image i) { return 0; } // alt text doesn't count
        public Integer table(Table t) { return t.rows().stream().flatMap(r -> r.stream()).mapToInt(WordCount::words).sum(); }
    }
}
