package com.handbook.behavioral.visitor.cart;

import com.handbook.behavioral.visitor.cart.CartItems.*;

/** Operation 1: GST per category. Rates live here, not in the item classes. */
public class GstVisitor implements CartVisitor {

    private double gst;

    public void visit(Book b) { gst += 0; }                          // books exempt
    public void visit(Electronics e) { gst += e.priceInr() * 0.18; }
    public void visit(FreshProduce f) { gst += 0; }                  // unprocessed produce exempt

    public double total() { return gst; }
}
