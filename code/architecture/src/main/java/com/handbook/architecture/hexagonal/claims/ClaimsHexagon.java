package com.handbook.architecture.hexagonal.claims;

import java.util.ArrayList;
import java.util.List;

/** The claims core with ONE driving port used by two very different driving adapters. */
public final class ClaimsHexagon {

    private ClaimsHexagon() {
    }

    public record Claim(String policy, String type, long amountPaise) {
    }

    /** Driving port. */
    public interface SubmitClaim {
        String submit(Claim claim);
    }

    /** The core: validation and a simple triage rule. */
    public static final class ClaimsCore implements SubmitClaim {
        private final List<Claim> accepted = new ArrayList<>();

        @Override
        public String submit(Claim c) {
            if (!c.policy().matches("POL-\\d{4}")) {
                return "REJECTED " + c.policy() + ": invalid policy number";
            }
            if (c.amountPaise() <= 0) {
                return "REJECTED " + c.policy() + ": amount must be positive";
            }
            accepted.add(c);
            String route = c.amountPaise() > 5_000_000 ? "manual review" : "fast track"; // (1)!
            return "ACCEPTED " + c.policy() + " " + c.type() + " -> " + route;
        }

        public int acceptedCount() {
            return accepted.size();
        }
    }

    /** Driving adapter 1: an event from the mobile app arrives on a message topic. */
    public static final class MobileClaimListener {
        private final SubmitClaim port;

        public MobileClaimListener(SubmitClaim port) {
            this.port = port;
        }

        public String onMessage(String json) { // {"policy":"POL-1234","type":"MOTOR","amount":"12500.00"}
            String body = json.replaceAll("[{}\"]", "");
            String policy = null;
            String type = null;
            long paise = 0;
            for (String field : body.split(",")) {
                String[] kv = field.split(":");
                switch (kv[0]) {
                    case "policy" -> policy = kv[1];
                    case "type" -> type = kv[1];
                    case "amount" -> paise = Math.round(Double.parseDouble(kv[1]) * 100);
                    default -> { }
                }
            }
            return "[topic] " + port.submit(new Claim(policy, type, paise));
        }
    }

    /** Driving adapter 2: a nightly CSV file from a partner garage network. */
    public static final class GarageCsvImporter {
        private final SubmitClaim port;

        public GarageCsvImporter(SubmitClaim port) {
            this.port = port;
        }

        public List<String> importFile(List<String> lines) {
            return lines.stream().skip(1) // header
                    .map(l -> l.split(";"))
                    .map(f -> "[csv]   " + port.submit(new Claim(f[0].trim(), "MOTOR", Long.parseLong(f[1].trim()))))
                    .toList();
        }
    }
}
