package com.handbook.structural.bridge.shapes;

import com.handbook.structural.bridge.shapes.Shapes.*;

import java.util.List;

public class ShapesDemo {

    public static void main(String[] args) {
        for (Renderer r : List.of(new SvgRenderer(), new AsciiRenderer())) {
            System.out.println(new Circle(r, 3).draw() + "  " + new Square(r, 4).draw());
        }
    }
}
