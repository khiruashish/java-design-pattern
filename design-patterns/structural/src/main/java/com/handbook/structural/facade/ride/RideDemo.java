package com.handbook.structural.facade.ride;

public class RideDemo {

    public static void main(String[] args) {
        RideBookingFacade rides = new RideBookingFacade();
        System.out.println(rides.book("R-9", "MG Road", 8, false));
        System.out.println(rides.book("R-9", "MG Road", 8, true));
        System.out.println(rides.book("R-9", "Airport T2", 35, false));
    }
}
