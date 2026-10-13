package com.handbook.architecture.hexagonal.fare;

import com.handbook.architecture.hexagonal.fare.FareHexagon.FallbackRouteAdapter;
import com.handbook.architecture.hexagonal.fare.FareHexagon.FareCalculator;
import com.handbook.architecture.hexagonal.fare.FareHexagon.GeoMapsAdapter;
import com.handbook.architecture.hexagonal.fare.FareHexagon.OfflineEstimateAdapter;
import com.handbook.architecture.hexagonal.fare.FareHexagon.Point;

import java.util.Map;

public class FareDemo {

    public static void main(String[] args) {
        Point koramangala = new Point(12.9352, 77.6245);
        Point airport = new Point(13.1986, 77.7066);
        boolean[] mapsUp = {true};
        var geoMaps = new GeoMapsAdapter(query -> {
            if (!mapsUp[0]) {
                throw new IllegalStateException("GeoMaps 503");
            }
            return Map.of("distance_m", 38_400L, "duration_s", 4_020L);
        });

        System.out.println("=== Ride-hailing: same fare core, swappable routing adapters ===");
        System.out.println("GeoMaps adapter  : " + new FareCalculator(geoMaps).quote(koramangala, airport));
        System.out.println("Offline adapter  : " + new FareCalculator(new OfflineEstimateAdapter()).quote(koramangala, airport));

        var resilient = new FareCalculator(new FallbackRouteAdapter(geoMaps, new OfflineEstimateAdapter()));
        mapsUp[0] = false;
        System.out.println("GeoMaps down     : " + resilient.quote(koramangala, airport));
    }
}
