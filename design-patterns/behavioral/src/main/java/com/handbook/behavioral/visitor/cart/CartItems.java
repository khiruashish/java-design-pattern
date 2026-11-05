package com.handbook.behavioral.visitor.cart;

/** Element hierarchy (stable) + visitor interface (operations grow). Classic double dispatch. */
public final class CartItems {

    private CartItems() {
    }

    public interface CartVisitor { // (1)!
        void visit(Book b);
        void visit(Electronics e);
        void visit(FreshProduce f);
    }

    public interface Item {
        void accept(CartVisitor v); // (2)!
    }

    public record Book(String title, int priceInr, int grams) implements Item {
        public void accept(CartVisitor v) { v.visit(this); }
    }

    public record Electronics(String name, int priceInr, int grams, boolean fragile) implements Item {
        public void accept(CartVisitor v) { v.visit(this); }
    }

    public record FreshProduce(String name, int priceInr, int grams, boolean chilled) implements Item {
        public void accept(CartVisitor v) { v.visit(this); }
    }
}
