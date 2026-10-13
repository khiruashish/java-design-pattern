package com.handbook.behavioral.template.jobs;

import java.util.HashSet;
import java.util.Set;

/** Challenge solution: every scheduled job gets locking, skipping and cleanup for free. */
public abstract class ScheduledJob {

    private static final Set<String> LOCKS = new HashSet<>(); // stand-in for a DB/Redis lock

    public final void execute() {
        if (!shouldRun()) {
            System.out.println(name() + ": skipped");
            return;
        }
        if (!LOCKS.add(name())) {
            System.out.println(name() + ": another instance is running, skipping");
            return;
        }
        try {
            System.out.println(name() + ": started");
            doWork();
            System.out.println(name() + ": finished");
        } catch (RuntimeException e) {
            System.out.println(name() + ": FAILED - " + e.getMessage());
        } finally {
            LOCKS.remove(name()); // always released, even on failure
        }
    }

    protected abstract String name();

    protected abstract void doWork();

    protected boolean shouldRun() { return true; }

    static boolean isLocked(String name) { return LOCKS.contains(name); }
}
