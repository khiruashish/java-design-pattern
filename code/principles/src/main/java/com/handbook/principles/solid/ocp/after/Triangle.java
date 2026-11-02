package com.handbook.principles.solid.ocp.after;

/** Added later. AreaCalculator was NOT modified to support it: open for extension, closed for modification. */
public record Triangle(double base, double height) implements Shape {
    public double area() { return 0.5 * base * height; }
    public String name() { return "triangle"; }
}
