package com.handbook.enterprise.domainevents.insurance;

import java.time.LocalDate;
import java.util.Map;

/** Domain events stay internal and rich; integration events are a public, versioned contract. */
public final class PolicyEvents {

    /** Internal: can change freely with the model. Contains data other teams must not depend on. */
    public record PolicyLapsed(String policyId, String customerId, LocalDate lapsedOn, int missedPremiums,
                               String underwriterNotes, double internalRiskScore) { }

    /** Public: what other services may rely on. Stable names, a schema version, no internals. */
    public record PolicyLapsedV1(int schemaVersion, String eventType, String policyId, String customerId, String lapsedOn) { }

    public static PolicyLapsedV1 toIntegrationEvent(PolicyLapsed e) { // (1)!
        return new PolicyLapsedV1(1, "insurance.policy.lapsed.v1", e.policyId(), e.customerId(), e.lapsedOn().toString());
    }

    public static Map<String, String> outboxRow(PolicyLapsedV1 e) { // (2)!
        return Map.of("topic", "insurance.policy.lapsed.v1", "key", e.policyId(),
                "payload", "{\"schemaVersion\":1,\"policyId\":\"" + e.policyId() + "\",\"customerId\":\"" + e.customerId()
                        + "\",\"lapsedOn\":\"" + e.lapsedOn() + "\"}");
    }

    private PolicyEvents() {
    }
}
