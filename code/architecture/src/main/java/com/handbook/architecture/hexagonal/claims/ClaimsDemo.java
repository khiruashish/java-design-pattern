package com.handbook.architecture.hexagonal.claims;

import com.handbook.architecture.hexagonal.claims.ClaimsHexagon.ClaimsCore;
import com.handbook.architecture.hexagonal.claims.ClaimsHexagon.GarageCsvImporter;
import com.handbook.architecture.hexagonal.claims.ClaimsHexagon.MobileClaimListener;

import java.util.List;

public class ClaimsDemo {

    public static void main(String[] args) {
        ClaimsCore core = new ClaimsCore();
        var listener = new MobileClaimListener(core);
        var importer = new GarageCsvImporter(core);

        System.out.println("=== Insurance: claims arrive by event and by nightly file, one core ===");
        System.out.println(listener.onMessage("{\"policy\":\"POL-1234\",\"type\":\"MOTOR\",\"amount\":\"12500.00\"}"));
        System.out.println(listener.onMessage("{\"policy\":\"PX-99\",\"type\":\"HEALTH\",\"amount\":\"800.00\"}"));
        importer.importFile(List.of(
                "policy;amount_paise",
                "POL-5678; 7500000",
                "POL-9012; 0")).forEach(System.out::println);
        System.out.println("Accepted claims: " + core.acceptedCount() + " (same rules for every channel)");
    }
}
