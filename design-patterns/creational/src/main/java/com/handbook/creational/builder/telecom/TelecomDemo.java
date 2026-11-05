package com.handbook.creational.builder.telecom;

public class TelecomDemo {

    public static void main(String[] args) {
        System.out.println("=== Telecom: One Recipe, Two Representations ===");
        PlanDirector director = new PlanDirector();

        MobilePlanBuilder billing = new MobilePlanBuilder();
        director.familyPlan(billing);
        System.out.println("Billing record: " + billing.result());

        BrochureBuilder brochure = new BrochureBuilder();
        director.familyPlan(brochure);
        System.out.println("Website brochure:\n" + brochure.result());

        MobilePlanBuilder student = new MobilePlanBuilder();
        director.studentPlan(student);
        System.out.println("Billing record: " + student.result());
    }
}
