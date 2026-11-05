package com.handbook.behavioral.interpreter.hours;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class HoursDemo {

    public static void main(String[] args) {
        System.out.println("=== Logistics: Warehouse Receiving Hours ===");
        OpeningHours bhiwandi = OpeningHours.parse("MON-FRI 09:00-18:00, SAT 10:00-14:00");
        OpeningHours nightHub = OpeningHours.parse("FRI-MON 22:00-23:59");

        Object[][] arrivals = {
                {DayOfWeek.WEDNESDAY, "11:30"}, {DayOfWeek.SATURDAY, "15:00"},
                {DayOfWeek.SUNDAY, "10:00"}, {DayOfWeek.SUNDAY, "22:30"}};
        for (Object[] a : arrivals) {
            DayOfWeek day = (DayOfWeek) a[0];
            LocalTime t = LocalTime.parse((String) a[1]);
            System.out.printf("Truck %-9s %s -> Bhiwandi: %-6s Night hub: %s%n", day, t,
                    bhiwandi.isOpen(day, t) ? "accept" : "wait", nightHub.isOpen(day, t) ? "accept" : "wait");
        }
    }
}
