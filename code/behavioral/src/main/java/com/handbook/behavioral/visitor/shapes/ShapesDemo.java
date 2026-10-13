package com.handbook.behavioral.visitor.shapes;

import java.util.List;

/** Challenge solution: area and perimeter as visitors over a closed shape hierarchy. */
public class ShapesDemo {

    interface ShapeVisitor<R> {
        R circle(Circle c);
        R rectangle(Rectangle r);
        R triangle(Triangle t);
    }

    interface Shape {
        <R> R accept(ShapeVisitor<R> v);
    }

    record Circle(double r) implements Shape {
        public <R> R accept(ShapeVisitor<R> v) { return v.circle(this); }
    }

    record Rectangle(double w, double h) implements Shape {
        public <R> R accept(ShapeVisitor<R> v) { return v.rectangle(this); }
    }

    record Triangle(double a, double b, double c) implements Shape {
        public <R> R accept(ShapeVisitor<R> v) { return v.triangle(this); }
    }

    static final ShapeVisitor<Double> AREA = new ShapeVisitor<>() {
        public Double circle(Circle c) { return Math.PI * c.r() * c.r(); }
        public Double rectangle(Rectangle r) { return r.w() * r.h(); }
        public Double triangle(Triangle t) { // Heron's formula
            double s = (t.a() + t.b() + t.c()) / 2;
            return Math.sqrt(s * (s - t.a()) * (s - t.b()) * (s - t.c()));
        }
    };

    static final ShapeVisitor<Double> PERIMETER = new ShapeVisitor<>() {
        public Double circle(Circle c) { return 2 * Math.PI * c.r(); }
        public Double rectangle(Rectangle r) { return 2 * (r.w() + r.h()); }
        public Double triangle(Triangle t) { return t.a() + t.b() + t.c(); }
    };

    public static void main(String[] args) {
        for (Shape s : List.of(new Circle(1), new Rectangle(3, 4), new Triangle(3, 4, 5))) {
            System.out.printf("%-30s area=%6.2f perimeter=%6.2f%n", s, s.accept(AREA), s.accept(PERIMETER));
        }
    }
}
