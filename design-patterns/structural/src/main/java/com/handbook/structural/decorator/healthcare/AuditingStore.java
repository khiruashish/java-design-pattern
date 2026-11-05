package com.handbook.structural.decorator.healthcare;

import java.util.ArrayList;
import java.util.List;

/** Decorator: records who touched which record, then delegates. */
public class AuditingStore implements RecordStore {

    private final RecordStore inner;
    private final String user;
    private final List<String> auditLog;

    public AuditingStore(RecordStore inner, String user, List<String> auditLog) {
        this.inner = inner;
        this.user = user;
        this.auditLog = auditLog;
    }

    public void write(String key, String data) {
        auditLog.add(user + " WRITE " + key);
        inner.write(key, data);
    }

    public String read(String key) {
        auditLog.add(user + " READ " + key);
        return inner.read(key);
    }

    public static List<String> newLog() {
        return new ArrayList<>();
    }
}
