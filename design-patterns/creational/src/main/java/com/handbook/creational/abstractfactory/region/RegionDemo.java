package com.handbook.creational.abstractfactory.region;

public class RegionDemo {

    public static void main(String[] args) {
        System.out.println("=== Grocery: One Checkout, Many Countries ===");
        CheckoutCalculator india = new CheckoutCalculator(new IndiaRegion());
        CheckoutCalculator germany = new CheckoutCalculator(new GermanyRegion());

        System.out.println("IN: " + india.summary(150_00));
        System.out.println("IN: " + india.summary(250_00));
        System.out.println("IN: " + india.summary(50_00));
        System.out.println("DE: " + germany.summary(50_00));
        System.out.println("DE: " + germany.summary(30_00));
    }
}
