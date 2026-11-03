package com.handbook.microservices.sharding.geo;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/** Directory-based sharding: a lookup table maps city (and, for hot cities, a zone range) to a shard. */
public final class GeoShardDirectory {

    private final Map<String, NavigableMap<Integer, String>> directory = new LinkedHashMap<>(); // city -> (zoneFrom -> shard)

    public void assign(String city, String shard) {
        directory.put(city, new TreeMap<>(Map.of(0, shard)));
    }

    /** Split a hot city: zones >= fromZone move to a new shard. Only that city's mapping changes. */
    public void split(String city, int fromZone, String newShard) { // (1)!
        directory.get(city).put(fromZone, newShard);
    }

    public String route(String city, int zone) {
        NavigableMap<Integer, String> ranges = directory.get(city);
        if (ranges == null) {
            throw new IllegalArgumentException("no shard for " + city);
        }
        return ranges.floorEntry(zone).getValue(); // (2)!
    }
}
