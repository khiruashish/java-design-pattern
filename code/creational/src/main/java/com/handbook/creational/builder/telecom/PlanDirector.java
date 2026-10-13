package com.handbook.creational.builder.telecom;

/** Director: owns the recipes. It knows the steps, not the representation. */
public class PlanDirector {

    public void studentPlan(PlanBuilder b) {
        b.name("Student Saver");
        b.monthlyPriceInr(199);
        b.data(1);
        b.unlimitedCalls();
        b.sms(100);
    }

    public void familyPlan(PlanBuilder b) {
        b.name("Family Max");
        b.monthlyPriceInr(999);
        b.data(3);
        b.unlimitedCalls();
        b.sms(100);
        b.addOn("OTT streaming bundle");
        b.addOn("International roaming 7 days");
    }
}
