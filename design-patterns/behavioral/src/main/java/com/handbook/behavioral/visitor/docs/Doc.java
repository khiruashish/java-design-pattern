package com.handbook.behavioral.visitor.docs;

import java.util.List;

/** Document elements with a generic visitor that returns a value. */
public final class Doc {

    private Doc() {
    }

    public interface Visitor<R> {
        R heading(Heading h);
        R paragraph(Paragraph p);
        R image(Image i);
        R table(Table t);
    }

    public interface Element {
        <R> R accept(Visitor<R> v);
    }

    public record Heading(int level, String text) implements Element {
        public <R> R accept(Visitor<R> v) { return v.heading(this); }
    }

    public record Paragraph(String text) implements Element {
        public <R> R accept(Visitor<R> v) { return v.paragraph(this); }
    }

    public record Image(String url, String alt) implements Element {
        public <R> R accept(Visitor<R> v) { return v.image(this); }
    }

    public record Table(List<String> header, List<List<String>> rows) implements Element {
        public <R> R accept(Visitor<R> v) { return v.table(this); }
    }
}
