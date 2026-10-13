package com.handbook.behavioral.template.admission;

public class AdmissionDemo {

    public static void main(String[] args) {
        System.out.println("=== Hospital: Patient Admission ===");
        System.out.println("Emergency (road accident):");
        new Admissions.Emergency().admit("P-7781").forEach(s -> System.out.println("  - " + s));
        System.out.println("Planned (knee surgery):");
        new Admissions.Planned().admit("P-1204").forEach(s -> System.out.println("  - " + s));
    }
}
