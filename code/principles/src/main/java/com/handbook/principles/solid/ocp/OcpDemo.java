package com.handbook.principles.solid.ocp;

import com.handbook.principles.solid.ocp.after.AreaCalculator;
import com.handbook.principles.solid.ocp.after.Circle;
import com.handbook.principles.solid.ocp.after.Rectangle;
import com.handbook.principles.solid.ocp.after.Shape;
import com.handbook.principles.solid.ocp.after.Triangle;

import java.util.List;

public class OcpDemo {
    public static void main(String[] args) {
        System.out.println("--- Before: instanceof chain ---");
        var before = new com.handbook.principles.solid.ocp.before.AreaCalculator();
        List<Object> oldShapes = List.of(new com.handbook.principles.solid.ocp.before.AreaCalculator.Circle(1),
                new com.handbook.principles.solid.ocp.before.AreaCalculator.Rectangle(2, 3));
        System.out.printf("total area = %.2f%n", before.totalArea(oldShapes));
        try {
            before.totalArea(List.of(new Object()));
        } catch (IllegalArgumentException e) {
            System.out.println("new shape without editing the calculator -> " + e.getMessage());
        }

        System.out.println();
        System.out.println("--- After: depend on the Shape abstraction ---");
        List<Shape> shapes = List.of(new Circle(1), new Rectangle(2, 3), new Triangle(4, 5));
        shapes.forEach(s -> System.out.printf("%-9s area = %.2f%n", s.name(), s.area()));
        System.out.printf("total area = %.2f (Triangle added without touching AreaCalculator)%n",
                new AreaCalculator().totalArea(shapes));
    }
}
