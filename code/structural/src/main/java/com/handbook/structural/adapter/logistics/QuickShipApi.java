package com.handbook.structural.adapter.logistics;

import java.util.Map;

/** Adaptee 2: a local courier that returns a map with numeric stages. */
public class QuickShipApi {

    public Map<String, Object> fetch(long consignmentNo) {
        return Map.of("stage", 2, "hub", "Hyderabad Hub");
    }
}
