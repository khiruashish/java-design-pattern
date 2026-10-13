package com.handbook.behavioral.visitor.docs;

import com.handbook.behavioral.visitor.docs.Doc.*;

import java.util.List;

public class DocsDemo {

    public static void main(String[] args) {
        System.out.println("=== Docs SaaS: One Document, Many Exports ===");
        List<Element> doc = List.of(
                new Heading(2, "Q3 Launch Plan"),
                new Paragraph("We launch in Pune and Jaipur on 1 November."),
                new Image("pune.png", "Pune dark store"),
                new Table(List.of("City", "Stores"), List.of(List.of("Pune", "12"), List.of("Jaipur", "7"))));

        System.out.println("--- Markdown ---");
        doc.forEach(e -> System.out.println(e.accept(new Exporters.Markdown())));
        System.out.println("--- HTML ---");
        doc.forEach(e -> System.out.println(e.accept(new Exporters.Html())));
        int words = doc.stream().mapToInt(e -> e.accept(new Exporters.WordCount())).sum();
        System.out.println("--- Word count: " + words + " ---");
    }
}
