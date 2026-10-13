package com.handbook.creational.abstractfactory.region;

public class JapanDemo {

    public static void main(String[] args) {
        CheckoutCalculator japan = new CheckoutCalculator(new JapanRegion());
        System.out.println("JP: " + japan.summary(3_000));
        System.out.println("JP: " + japan.summary(1_500));
        System.out.println("JP: " + japan.summary(800));
    }
}
