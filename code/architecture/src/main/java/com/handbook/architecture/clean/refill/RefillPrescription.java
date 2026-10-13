package com.handbook.architecture.clean.refill;

import java.time.LocalDate;
import java.util.Map;

/**
 * A modern-Java variation: instead of an output boundary, the use case returns a sealed Result.
 * The web layer (outermost circle) maps each case to HTTP. Same dependency rule: inner code never
 * mentions HTTP.
 */
public final class RefillPrescription {

    private RefillPrescription() {
    }

    public record Prescription(String id, String patient, String drug, int refillsLeft, LocalDate validUntil,
                               boolean controlledSubstance) {
    }

    public sealed interface Result permits Approved, NeedsDoctor, Rejected {
    }

    public record Approved(String drug, int refillsLeft) implements Result {
    }

    public record NeedsDoctor(String reason) implements Result {
    }

    public record Rejected(String reason) implements Result {
    }

    public interface Prescriptions {
        Prescription find(String id);
    }

    /** Use case: pure rules + gateway calls; trivially unit-testable. */
    public static Result refill(Prescriptions repo, String prescriptionId, String patient, LocalDate today) {
        Prescription p = repo.find(prescriptionId);
        if (p == null || !p.patient().equals(patient)) {
            return new Rejected("no such prescription for this patient"); // (1)!
        }
        if (today.isAfter(p.validUntil())) {
            return new NeedsDoctor("prescription expired on " + p.validUntil());
        }
        if (p.controlledSubstance()) {
            return new NeedsDoctor("controlled substance: doctor must approve each refill");
        }
        if (p.refillsLeft() == 0) {
            return new NeedsDoctor("no refills left");
        }
        return new Approved(p.drug(), p.refillsLeft() - 1);
    }

    /** Outermost circle: the web controller maps results to HTTP. */
    public static String httpResponse(Result result) {
        return switch (result) { // (2)!
            case Approved a -> "200 refill approved: " + a.drug() + " (" + a.refillsLeft() + " left)";
            case NeedsDoctor n -> "202 sent to your doctor: " + n.reason();
            case Rejected r -> "404 " + r.reason();
        };
    }

    public static Prescriptions inMemory(Map<String, Prescription> data) {
        return data::get;
    }
}
