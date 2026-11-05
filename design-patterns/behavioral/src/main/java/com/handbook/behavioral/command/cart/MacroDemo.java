package com.handbook.behavioral.command.cart;

import com.handbook.behavioral.command.cart.CartCommands.*;

import java.util.List;

public class MacroDemo {

    public static void main(String[] args) {
        Cart cart = new Cart();
        new AddItem(cart, "BANANA-6", 1).execute();
        System.out.println("Cart before        : " + cart);

        Macro usual = new Macro(List.of(
                new AddItem(cart, "MILK-1L", 2), new AddItem(cart, "BREAD-WW", 1), new AddItem(cart, "BANANA-6", 1)));
        usual.execute();
        System.out.println("After 'my usual'   : " + cart);
        usual.undo();
        System.out.println("After undo         : " + cart);
    }
}
