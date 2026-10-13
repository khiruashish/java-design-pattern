package com.handbook.lld.elevator;

import com.handbook.lld.elevator.ElevatorSystem.Direction;

public class ElevatorDemo {

    public static void main(String[] args) {
        var building = new ElevatorSystem(ElevatorSystem.nearestSuitable(), 0, 8);

        System.out.println("=== LLD: Elevator system (2 cars, SCAN scheduling) ===");
        System.out.println(building.hallCall(3, Direction.UP));
        System.out.println(building.hallCall(7, Direction.DOWN));
        for (int t = 1; t <= 4; t++) {
            if (t == 2) {
                building.carCall("E1", 6); // passenger who got in at 3 presses 6
                System.out.println("   (passenger in E1 presses 6)");
            }
            System.out.println("t=" + t + " " + building.tick());
        }
        System.out.println(building.hallCall(5, Direction.UP) + "  (E1 is moving up past 5)");
        for (int t = 5; t <= 7; t++) {
            System.out.println("t=" + t + " " + building.tick());
        }
    }
}
