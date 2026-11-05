package com.handbook.behavioral.visitor.cart;

import com.handbook.behavioral.visitor.cart.CartItems.*;

/** Operation 2: shipping cost. Each item type has its own rule. */
public class ShippingVisitor implements CartVisitor {

    private int grams;
    private int surcharges;

    public void visit(Book b) { grams += b.grams(); }

    public void visit(Electronics e) {
        grams += e.grams();
        if (e.fragile()) {
            surcharges += 40; // bubble wrap and careful handling
        }
    }

    public void visit(FreshProduce f) {
        grams += f.grams();
        if (f.chilled()) {
            surcharges += 25; // cold-chain bag
        }
    }

    public int costInr() {
        return 30 + (grams / 1000) * 10 + surcharges;
    }
}
