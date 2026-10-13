package com.handbook.structural.decorator.healthcare;

import java.util.HashMap;
import java.util.Map;

/** Concrete component: stores exactly what it is given (think: object storage). */
public class InMemoryRecordStore implements RecordStore {

    private final Map<String, String> data = new HashMap<>();

    public void write(String key, String value) { data.put(key, value); }

    public String read(String key) { return data.get(key); }

    public String raw(String key) { return data.get(key); }
}
