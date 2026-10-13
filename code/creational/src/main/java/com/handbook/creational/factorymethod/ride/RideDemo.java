package com.handbook.creational.factorymethod.ride;

public class RideDemo {

    public static void main(String[] args) {
        for (RideType type : RideType.values()) {
            System.out.printf("%-5s  3 km = INR %3d | 10 km = INR %3d%n",
                    type, FareCalculator.of(type).fareInr(3), FareCalculator.of(type).fareInr(10));
        }
    }
}
