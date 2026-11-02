package com.handbook.principles.oop.abstraction;

import java.nio.charset.StandardCharsets;

/** Client written against the abstraction only. */
public class ReportArchiver {
    private final FileStorage storage;

    public ReportArchiver(FileStorage storage) { this.storage = storage; }

    public String archiveAndReadBack(String name, String body) {
        storage.put(name, body.getBytes(StandardCharsets.UTF_8));
        return storage.get(name).map(b -> new String(b, StandardCharsets.UTF_8)).orElse("<missing>");
    }
}
