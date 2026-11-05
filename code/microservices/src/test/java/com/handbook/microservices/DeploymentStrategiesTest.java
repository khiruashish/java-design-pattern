package com.handbook.microservices;

import com.handbook.microservices.deployment.bluegreen.BlueGreenRouter;
import com.handbook.microservices.deployment.canary.CanaryAnalysis;
import com.handbook.microservices.deployment.toggles.FeatureToggles;
import com.handbook.microservices.deployment.toggles.FeatureToggles.Rule;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DeploymentStrategiesTest {

    @Test
    void rollbackRestoresTheOldVersionInOneStep() {
        BlueGreenRouter r = new BlueGreenRouter(x -> "old");
        r.deployToIdle(x -> "new");
        r.switchTraffic();
        assertThat(r.handle("x")).isEqualTo("green: new");
        r.switchTraffic();
        assertThat(r.handle("x")).isEqualTo("blue: old");
    }

    @Test
    void badCanaryIsRolledBackEarly() {
        var results = new CanaryAnalysis(new int[] {1, 5, 25, 100}, 0.5).run(0.001, 0.2, 1);
        assertThat(results.get(results.size() - 1).decision()).isEqualTo("ROLLBACK");
        assertThat(results.get(results.size() - 1).percent()).isLessThan(100);
    }

    @Test
    void togglesAreStickyAndKillSwitchWins() {
        FeatureToggles t = new FeatureToggles();
        t.set("f", new Rule(false, Set.of("beta"), 30));
        boolean first = t.isOn("f", "u1", "x");
        assertThat(t.isOn("f", "u1", "x")).isEqualTo(first);
        assertThat(t.isOn("f", "u2", "beta")).isTrue();
        t.set("f", new Rule(true, Set.of("beta"), 100));
        assertThat(t.isOn("f", "u2", "beta")).isFalse();
    }
}
