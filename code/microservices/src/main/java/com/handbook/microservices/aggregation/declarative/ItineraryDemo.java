package com.handbook.microservices.aggregation.declarative;

import com.handbook.microservices.aggregation.core.SimCall;
import com.handbook.microservices.aggregation.declarative.AggregationSpec.FieldMapping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ItineraryDemo {

    public static void main(String[] args) {
        System.out.println("=== Travel: One /itinerary Call Instead of Four from the Mobile App ===");
        AggregationSpec spec = new AggregationSpec(List.of( // would live in gateway YAML, owned by the API team
                new FieldMapping("flight", "flights", "status"),
                new FieldMapping("gate", "flights", "gate"),
                new FieldMapping("hotel", "hotels", "name"),
                new FieldMapping("checkIn", "hotels", "checkIn"),
                new FieldMapping("cab", "cabs", "pickup"),
                new FieldMapping("weather", "weather", "summary")));

        Map<String, SimCall> backends = new LinkedHashMap<>();
        backends.put("flights", SimCall.ok("flights", 120, Map.of("status", "6E-2134 on time", "gate", "B12")));
        backends.put("hotels", SimCall.ok("hotels", 90, Map.of("name", "Sea Breeze, Goa", "checkIn", "14:00")));
        backends.put("cabs", SimCall.ok("cabs", 60, Map.of("pickup", "GOI T1, 11:20")));
        backends.put("weather", SimCall.ok("weather", 40, Map.of("summary", "31C, humid")));

        StringBuilder timeline = new StringBuilder();
        Map<String, Object> itinerary = spec.execute(backends, timeline);
        System.out.println("Response: " + itinerary);
        System.out.println("Gateway: " + timeline + "; the app on 4G would have made 4 round trips (~4 x 300 ms).");
    }
}
