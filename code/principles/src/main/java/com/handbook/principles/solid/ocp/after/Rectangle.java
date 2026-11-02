package com.handbook.principles.solid.ocp.after;

public record Rectangle(double width, double height) implements Shape {
    public double area() { return width * height; }
    public String name() { return "rectangle"; }
}
