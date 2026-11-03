package com.handbook.microservices.deployment.toggles;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.CRC32;

/** Release toggles decoupled from deploys: segment targeting, sticky percentage rollout, and a kill switch. */
public final class FeatureToggles {

    public record Rule(boolean killSwitchOn, Set<String> segments, int percent) { }

    private final Map<String, Rule> rules = new HashMap<>();

    public void set(String feature, Rule rule) {
        rules.put(feature, rule);
    }

    public boolean isOn(String feature, String userId, String segment) {
        Rule r = rules.get(feature);
        if (r == null || r.killSwitchOn()) { // (1)!
            return false;
        }
        if (r.segments().contains(segment)) { // (2)!
            return true;
        }
        return bucket(feature, userId) < r.percent(); // (3)!
    }

    /** Stable 0..99 bucket per (feature, user): the same user always gets the same answer. */
    static int bucket(String feature, String userId) {
        CRC32 crc = new CRC32();
        crc.update((feature + ":" + userId).getBytes(StandardCharsets.UTF_8));
        return (int) (crc.getValue() % 100);
    }
}
