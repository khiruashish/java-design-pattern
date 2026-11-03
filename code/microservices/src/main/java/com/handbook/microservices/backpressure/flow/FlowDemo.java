package com.handbook.microservices.backpressure.flow;

import java.util.ArrayList;
import java.util.List;

public class FlowDemo {

    public static void main(String[] args) {
        System.out.println("=== Ride-hailing: Map-Matching GPS Pings at the Consumer's Pace ===");
        List<String> pings = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            pings.add("p" + i);
        }
        BatchingSubscriber matcher = new BatchingSubscriber(3);
        new GpsPublisher(pings).subscribe(matcher);
        matcher.log().forEach(l -> System.out.println("  " + l));
        System.out.println("The publisher never pushed more than the 3 pings requested at a time.");
    }
}
