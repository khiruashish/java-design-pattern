package com.handbook.principles.oop.abstraction;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryStorage implements FileStorage {
    private final Map<String, byte[]> files = new HashMap<>();

    public void put(String key, byte[] content) { files.put(key, content.clone()); }

    public Optional<byte[]> get(String key) { return Optional.ofNullable(files.get(key)).map(byte[]::clone); }

    public String describe() { return "in-memory (" + files.size() + " files)"; }
}
