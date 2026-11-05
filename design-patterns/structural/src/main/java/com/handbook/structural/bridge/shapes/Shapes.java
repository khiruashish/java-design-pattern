package com.handbook.structural.bridge.shapes;

/** Challenge solution: abstraction hierarchy + two implementors, all in one file. */
public final class Shapes {

    private Shapes() {
    }

    public abstract static class Shape {
        protected final Renderer renderer;

        protected Shape(Renderer renderer) {
            this.renderer = renderer;
        }

        public abstract String draw();
    }

    public static class Circle extends Shape {
        private final double radius;

        public Circle(Renderer r, double radius) {
            super(r);
            this.radius = radius;
        }

        public String draw() { return renderer.circle(radius); }
    }

    public static class Square extends Shape {
        private final double side;

        public Square(Renderer r, double side) {
            super(r);
            this.side = side;
        }

        public String draw() { return renderer.square(side); }
    }

    public static class SvgRenderer implements Renderer {
        public String circle(double r) { return "<circle r=\"%.0f\"/>".formatted(r); }
        public String square(double s) { return "<rect width=\"%.0f\" height=\"%.0f\"/>".formatted(s, s); }
    }

    public static class AsciiRenderer implements Renderer {
        public String circle(double r) { return "( " + "o".repeat((int) r) + " )"; }
        public String square(double s) { return "[" + "#".repeat((int) s) + "]"; }
    }
}
