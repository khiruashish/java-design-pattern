package com.handbook.structural.flyweight.ridemap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Flyweight factory: creates each icon once and hands out the shared instance. */
public final class IconFactory {

    private final Map<String, VehicleIcon> icons = new ConcurrentHashMap<>();

    public VehicleIcon icon(String type) {
        return icons.computeIfAbsent(type, VehicleIcon::new); // (1)!
    }

    public int distinctIcons() {
        return icons.size();
    }
}
