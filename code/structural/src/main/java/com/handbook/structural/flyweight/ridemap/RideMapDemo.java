package com.handbook.structural.flyweight.ridemap;

import java.util.ArrayList;
import java.util.List;

public class RideMapDemo {

    public static void main(String[] args) {
        System.out.println("=== Ride-hailing: 10,000 Live Cars on the City Map ===");
        IconFactory factory = new IconFactory();
        String[] types = {"Auto", "Mini", "Sedan"};
        List<CarMarker> markers = new ArrayList<>();

        for (int i = 0; i < 10_000; i++) {
            double lat = 12.90 + (i % 100) * 0.001;
            double lng = 77.55 + (i / 100) * 0.001;
            markers.add(new CarMarker("D" + i, lat, lng, (i * 37) % 360, factory.icon(types[i % 3])));
        }

        markers.subList(0, 3).forEach(m -> System.out.println(m.render()));
        long iconBytes = factory.icon("Mini").sizeBytes();
        long withoutFlyweight = markers.size() * iconBytes;
        long withFlyweight = factory.distinctIcons() * iconBytes + markers.size() * 48L; // ~48 B per marker
        System.out.println("Markers on map       : " + markers.size());
        System.out.println("Icon objects created : " + factory.distinctIcons());
        System.out.printf("Memory without sharing: ~%,d KB%n", withoutFlyweight / 1024);
        System.out.printf("Memory with flyweight : ~%,d KB%n", withFlyweight / 1024);
        System.out.println("Same Mini icon shared? " + (markers.get(1).icon() == markers.get(4).icon()));
    }
}
