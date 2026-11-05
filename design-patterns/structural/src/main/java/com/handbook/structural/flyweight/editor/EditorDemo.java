package com.handbook.structural.flyweight.editor;

public class EditorDemo {

    public static void main(String[] args) {
        System.out.println("=== Docs SaaS: Styled Text in a 3-Page Contract ===");
        Document doc = new Document();
        for (int page = 1; page <= 3; page++) {
            doc.type("Clause " + page + ": ", TextStyle.of("Inter", 12, true, false, "#111111"));
            doc.type("The supplier shall deliver goods within 10 days. ".repeat(20),
                    TextStyle.of("Inter", 12, false, false, "#111111"));
            doc.type("Confidential", TextStyle.of("Inter", 10, false, true, "#888888"));
        }
        System.out.println("Characters in document   : " + doc.length());
        System.out.println("Style objects referenced : " + doc.distinctStyleObjects());
        System.out.println("Styles in the pool       : " + TextStyle.pooled());
        System.out.println("Bold style interned?     : "
                + (TextStyle.of("Inter", 12, true, false, "#111111") == TextStyle.of("Inter", 12, true, false, "#111111")));
    }
}
