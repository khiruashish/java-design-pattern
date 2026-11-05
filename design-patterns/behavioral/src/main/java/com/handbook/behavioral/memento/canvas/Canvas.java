package com.handbook.behavioral.memento.canvas;

import java.util.ArrayList;
import java.util.List;

/** Originator: a design canvas. Only it can create and read its snapshots. */
public class Canvas {

    public record Shape(String kind, int x, int y, String color) {
    }

    private final List<Shape> shapes = new ArrayList<>();
    private String background = "white";

    public void add(Shape s) { shapes.add(s); }

    public void recolorAll(String color) {
        shapes.replaceAll(s -> new Shape(s.kind(), s.x(), s.y(), color));
    }

    public void setBackground(String bg) { this.background = bg; }

    /** Memento: immutable, and its contents are private to Canvas (nested class, private accessors). */
    public static final class Snapshot { // (1)!
        private final List<Shape> shapes;
        private final String background;
        private final String label;

        private Snapshot(List<Shape> shapes, String background, String label) {
            this.shapes = List.copyOf(shapes); // (2)!
            this.background = background;
            this.label = label;
        }

        public String label() { return label; } // the only thing the caretaker may see
    }

    public Snapshot save(String label) {
        return new Snapshot(shapes, background, label);
    }

    public void restore(Snapshot s) {
        shapes.clear();
        shapes.addAll(s.shapes);
        background = s.background;
    }

    @Override
    public String toString() {
        return "bg=" + background + " " + shapes.stream().map(s -> s.kind() + "(" + s.color() + ")").toList();
    }
}
