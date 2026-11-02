package com.handbook.principles.solid.ocp.before;

import java.util.List;

/**
 * VIOLATES OCP: every new shape forces an edit to this (already tested, already shipped) method.
 * Shapes are plain data holders; the knowledge of "how to compute an area" is centralised here.
 */
public class AreaCalculator {

    public record Circle(double radius) { }
    public record Rectangle(double width, double height) { }

    public double totalArea(List<Object> shapes) {
        double total = 0;
        for (Object shape : shapes) {
            if (shape instanceof Circle c) {
                total += Math.PI * c.radius() * c.radius();
            } else if (shape instanceof Rectangle r) {
                total += r.width() * r.height();
            } else {
                // Adding Triangle means editing this method, re-testing it and redeploying it.
                throw new IllegalArgumentException("Unsupported shape: " + shape.getClass().getSimpleName());
            }
        }
        return total;
    }
}
