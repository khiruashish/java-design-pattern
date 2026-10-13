package com.handbook.behavioral.memento.canvas;

import com.handbook.behavioral.memento.canvas.Canvas.Shape;

public class CanvasDemo {

    public static void main(String[] args) {
        System.out.println("=== Design SaaS: Named Version History ===");
        Canvas canvas = new Canvas();
        VersionHistory history = new VersionHistory();

        canvas.add(new Shape("logo", 10, 10, "blue"));
        canvas.add(new Shape("headline", 10, 80, "black"));
        history.keep(canvas.save("v1 draft"));

        canvas.setBackground("navy");
        canvas.recolorAll("white");
        history.keep(canvas.save("v2 dark theme"));

        canvas.add(new Shape("banner", 0, 200, "orange"));
        canvas.recolorAll("pink"); // the client hates it
        System.out.println("current      : " + canvas);
        System.out.println("versions     : " + history.labels());

        canvas.restore(history.get("v2 dark theme"));
        System.out.println("restore v2   : " + canvas);
        canvas.restore(history.get("v1 draft"));
        System.out.println("restore v1   : " + canvas);
    }
}
