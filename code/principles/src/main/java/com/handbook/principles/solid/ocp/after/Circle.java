package com.handbook.principles.solid.ocp.after;

public record Circle(double radius) implements Shape {
    public double area() { return Math.PI * radius * radius; }
    public String name() { return "circle"; }
}
