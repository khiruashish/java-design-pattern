package com.handbook.behavioral.template.admission;

public final class Admissions {

    private Admissions() {
    }

    public static class Emergency extends Admission {
        protected void triage(String p) { steps.add("triage level 1 (critical), doctor paged"); }
        protected String assignBed() { return "ICU-04"; }
        @Override protected boolean needsInsurancePreAuth() { return false; } // treat first, paperwork later
        @Override protected int depositInr() { return 0; }
    }

    public static class Planned extends Admission {
        protected void triage(String p) { steps.add("pre-op checklist verified"); }
        protected String assignBed() { return "WARD-2B-17"; }
    }
}
