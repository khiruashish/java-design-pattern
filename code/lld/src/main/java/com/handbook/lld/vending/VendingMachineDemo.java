package com.handbook.lld.vending;

import com.handbook.lld.vending.VendingMachine.Product;

public class VendingMachineDemo {

    public static void main(String[] args) {
        var vm = new VendingMachine()
                .load("A1", new Product("Masala chips", 20), 2)
                .load("B1", new Product("Mango juice", 35), 0);

        System.out.println("=== LLD: Vending machine (State pattern) ===");
        System.out.println("select A1       -> " + vm.select("A1"));
        System.out.println("insert ₹10      -> " + vm.insert(10));
        System.out.println("select A1       -> " + vm.select("A1"));
        System.out.println("insert ₹3       -> " + vm.insert(3));
        System.out.println("insert ₹10      -> " + vm.insert(10));
        System.out.println("insert ₹5       -> " + vm.insert(5));
        System.out.println("insert ₹2       -> " + vm.insert(2));
        System.out.println("select B1       -> " + vm.select("B1"));
        System.out.println("select A1       -> " + vm.select("A1"));
        System.out.println("insert ₹10      -> " + vm.insert(10));
        System.out.println("cancel          -> " + vm.cancel());
    }
}
