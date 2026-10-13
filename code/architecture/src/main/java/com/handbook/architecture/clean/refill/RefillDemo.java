package com.handbook.architecture.clean.refill;

import com.handbook.architecture.clean.refill.RefillPrescription.Prescription;

import java.time.LocalDate;
import java.util.Map;

public class RefillDemo {

    public static void main(String[] args) {
        var repo = RefillPrescription.inMemory(Map.of(
                "RX-1", new Prescription("RX-1", "PAT-5", "Metformin 500 mg", 3, LocalDate.of(2027, 3, 1), false),
                "RX-2", new Prescription("RX-2", "PAT-5", "Alprazolam 0.25 mg", 2, LocalDate.of(2027, 3, 1), true),
                "RX-3", new Prescription("RX-3", "PAT-5", "Atorvastatin 10 mg", 1, LocalDate.of(2026, 9, 30), false)));
        LocalDate today = LocalDate.of(2026, 10, 14);

        System.out.println("=== Healthcare: refill use case returning a sealed Result ===");
        for (String[] req : new String[][] {{"RX-1", "PAT-5"}, {"RX-2", "PAT-5"}, {"RX-3", "PAT-5"}, {"RX-1", "PAT-9"}}) {
            var result = RefillPrescription.refill(repo, req[0], req[1], today);
            System.out.printf("%s by %s -> %s%n", req[0], req[1], RefillPrescription.httpResponse(result));
        }
    }
}
