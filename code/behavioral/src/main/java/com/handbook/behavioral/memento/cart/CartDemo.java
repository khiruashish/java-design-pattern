package com.handbook.behavioral.memento.cart;

public class CartDemo {

    public static void main(String[] args) {
        System.out.println("=== Grocery: Safe Substitutions and 'Undo Clear Cart' ===");
        Cart cart = new Cart();
        cart.add("MILK-1L", 2);
        cart.add("PANEER-200G", 1);
        cart.add("ATTA-5KG", 1);
        System.out.println("cart             : " + cart);

        // A multi-step substitution: paneer out of stock -> swap for tofu + apply a bundle coupon
        Cart.Checkpoint before = cart.checkpoint();
        try {
            cart.remove("PANEER-200G");
            cart.add("TOFU-200G", 1);
            cart.applyCoupon("SWAP10");
            throw new IllegalStateException("TOFU-200G also out of stock"); // step 3 fails
        } catch (IllegalStateException e) {
            cart.rollback(before);
            System.out.println("substitution failed (" + e.getMessage() + "), rolled back");
        }
        System.out.println("cart             : " + cart);

        Cart.Checkpoint undo = cart.checkpoint();
        cart.clear();
        System.out.println("after clear      : " + cart + "   [toast: Cart cleared. UNDO]");
        cart.rollback(undo);
        System.out.println("after UNDO       : " + cart);
    }
}
