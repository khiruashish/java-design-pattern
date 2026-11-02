package com.handbook.principles.solid.ocp.after;

import java.util.List;

/** Closed for modification: it depends only on the Shape abstraction. */
public class AreaCalculator {
    public double totalArea(List<? extends Shape> shapes) {
        return shapes.stream().mapToDouble(Shape::area).sum();
    }
}
