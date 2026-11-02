package com.handbook.principles.oop.inheritance;

import java.util.List;

public class InheritanceDemo {
    public static void main(String[] args) {
        List<Vehicle> lane = List.of(new Car("KA-01-1234"), new Truck("MH-12-9876", 3));
        lane.forEach(v -> System.out.println(v.describe()));
    }
}
