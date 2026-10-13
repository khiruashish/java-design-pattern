package com.handbook.behavioral.nullobject.audit;

import java.util.ArrayList;
import java.util.List;

/** Where audit events go. Free-plan tenants get the null object. */
public interface AuditSink {

    void record(String actor, String action);

    AuditSink DISABLED = (actor, action) -> { }; // (1)!

    final class Recording implements AuditSink {
        private final List<String> events = new ArrayList<>();

        public void record(String actor, String action) {
            events.add(actor + " " + action);
        }

        public List<String> events() {
            return events;
        }
    }

    static AuditSink forPlan(String plan) {
        return plan.equals("enterprise") ? new Recording() : DISABLED;
    }
}
