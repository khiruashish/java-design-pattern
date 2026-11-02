package com.handbook.principles.solid.lsp;

import com.handbook.principles.solid.lsp.after.Rectangle;
import com.handbook.principles.solid.lsp.after.Shape;
import com.handbook.principles.solid.lsp.after.Square;
import com.handbook.principles.solid.lsp.before.Resizer;

import java.util.List;

public class LspDemo {
    public static void main(String[] args) {
        System.out.println("--- Before: Square extends Rectangle ---");
        Resizer resizer = new Resizer();
        System.out.println("Rectangle resized to 5x4 -> area " + resizer.resizeTo5x4(new com.handbook.principles.solid.lsp.before.Rectangle()));
        System.out.println("Square    resized to 5x4 -> area " + resizer.resizeTo5x4(new com.handbook.principles.solid.lsp.before.Square())
                + "   <-- expected 20: substituting the subtype broke the caller");

        System.out.println();
        System.out.println("--- After: immutable shapes share only what is true for both ---");
        List<Shape> shapes = List.of(new Rectangle(5, 4), new Square(4), new Rectangle(2, 3).withSize(5, 4));
        for (Shape s : shapes) {
            String detail = switch (s) {                         // sealed: the compiler checks every case
                case Rectangle r -> "rectangle " + r.width() + "x" + r.height();
                case Square sq -> "square " + sq.side() + "x" + sq.side();
            };
            System.out.println(detail + " -> area " + s.area());
        }
    }
}
