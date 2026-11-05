package com.handbook.creational.factorymethod.logistics;

public class LogisticsDemo {

    public static void main(String[] args) {
        System.out.println("=== Logistics: Shipment Planning ===");
        print(new RoadLogistics(), "SHP-1", "Bengaluru->Mysuru", 145);
        print(new RailLogistics(), "SHP-2", "Delhi->Mumbai", 1400);
        print(new AirLogistics(), "SHP-3", "Chennai->Guwahati", 2650);
    }

    private static void print(LogisticsPlanner planner, String id, String route, int km) {
        System.out.println(planner.plan(id, route, km)); // same call, different transport
    }
}
