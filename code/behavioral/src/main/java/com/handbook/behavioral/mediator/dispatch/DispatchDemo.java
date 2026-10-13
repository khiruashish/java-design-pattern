package com.handbook.behavioral.mediator.dispatch;

import com.handbook.behavioral.mediator.dispatch.Dispatch.*;

public class DispatchDemo {

    public static void main(String[] args) {
        System.out.println("=== Ride-hailing: Dispatch Center ===");
        CityDispatch center = new CityDispatch();
        Driver ramesh = new Driver("Ramesh", 2, center);
        Driver sunita = new Driver("Sunita", 9, center);

        new Rider("Asha", 8, center).requestRide();
        new Rider("Vikram", 3, center).requestRide();
        new Rider("Meera", 5, center).requestRide();
        sunita.finishTrip();
    }
}
