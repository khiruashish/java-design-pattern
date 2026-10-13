package com.handbook.architecture.modular.verify;

import com.handbook.architecture.modular.verify.ModuleVerifier.Reference;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ModuleVerifyDemo {

    public static void main(String[] args) {
        var verifier = new ModuleVerifier(Map.of(
                "enrollment", Set.of("courses"),
                "certificates", Set.of("enrollment"),
                "payments", Set.of()));

        List<Reference> found = List.of(
                new Reference("enrollment", "EnrollmentService", "courses", "lms.courses"),
                new Reference("certificates", "CertificateIssuer", "enrollment", "lms.enrollment"),
                new Reference("certificates", "CertificatePdf", "courses", "lms.courses"),
                new Reference("enrollment", "SeatChecker", "courses", "lms.courses.internal.jpa"),
                new Reference("payments", "RefundPolicy", "enrollment", "lms.enrollment"));

        System.out.println("=== Education platform: verifying module boundaries in CI ===");
        List<String> problems = verifier.verify(found);
        problems.forEach(p -> System.out.println("FAIL " + p));
        System.out.println(problems.isEmpty() ? "All module boundaries respected" : problems.size() + " violations: build fails");
    }
}
