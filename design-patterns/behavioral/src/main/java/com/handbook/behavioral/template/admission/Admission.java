package com.handbook.behavioral.template.admission;

import java.util.ArrayList;
import java.util.List;

/** Template for admitting a patient. Emergency and planned admissions differ in a few steps. */
public abstract class Admission {

    protected final List<String> steps = new ArrayList<>();

    public final List<String> admit(String patient) {
        steps.clear();
        register(patient);
        triage(patient);
        if (needsInsurancePreAuth()) { // hook
            steps.add("insurance pre-authorization requested");
        }
        steps.add("bed assigned: " + assignBed());
        steps.add("billing account opened" + (depositInr() > 0 ? ", deposit INR " + depositInr() : ", deposit deferred"));
        return List.copyOf(steps);
    }

    private void register(String patient) {
        steps.add("registered " + patient);
    }

    protected abstract void triage(String patient);

    protected abstract String assignBed();

    protected boolean needsInsurancePreAuth() { return true; }

    protected int depositInr() { return 20_000; }
}
