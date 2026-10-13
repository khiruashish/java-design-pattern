package com.handbook.creational.singleton.healthcare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Central audit trail for access to patient records (compliance requirement:
 * every read/write must be recorded in one ordered log).
 * Implemented with lazy Double-Checked Locking.
 */
public final class AuditLogger {

    private static volatile AuditLogger instance; // (1)!

    private final List<String> entries = new ArrayList<>();

    private AuditLogger() {
        if (instance != null) { // (2)!
            throw new IllegalStateException("Use AuditLogger.getInstance()");
        }
        System.out.println("[AuditLogger] Opening audit store...");
    }

    public static AuditLogger getInstance() {
        AuditLogger local = instance; // (3)!
        if (local == null) {
            synchronized (AuditLogger.class) {
                local = instance;
                if (local == null) {
                    instance = local = new AuditLogger();
                }
            }
        }
        return local;
    }

    public synchronized void record(String user, String action, String patientId) {
        entries.add("#%03d %-10s %-14s patient=%s"
                .formatted(entries.size() + 1, user, action, patientId));
    }

    public synchronized List<String> entries() {
        return Collections.unmodifiableList(new ArrayList<>(entries));
    }
}
