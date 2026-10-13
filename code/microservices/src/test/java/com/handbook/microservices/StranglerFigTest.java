package com.handbook.microservices;

import com.handbook.microservices.strangler.banking.PercentageRollout;
import com.handbook.microservices.strangler.grocery.StranglerRouter;
import com.handbook.microservices.strangler.insurance.ShadowComparator;
import com.handbook.microservices.support.Http.Request;
import com.handbook.microservices.support.Http.Response;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class StranglerFigTest {

    @Test
    void migratedRoutesGoToNewServiceOthersToLegacy() {
        var router = new StranglerRouter(r -> Response.of(200, "legacy"));
        router.migrate("/cart", r -> Response.of(200, "new"));
        assertThat(router.handle(Request.get("/cart/1", Map.of())).body()).isEqualTo("new");
        assertThat(router.handle(Request.get("/orders/1", Map.of())).body()).isEqualTo("legacy");
    }

    @Test
    void rolloutIsStickyPerCustomer() {
        var rollout = new PercentageRollout(id -> "legacy", id -> "modern", Set.of());
        rollout.setPercent(50);
        String first = rollout.statement("CUST-1");
        for (int i = 0; i < 10; i++) {
            assertThat(rollout.statement("CUST-1")).isEqualTo(first);
        }
    }

    @Test
    void shadowAlwaysReturnsLegacyAnswer() {
        var shadow = new ShadowComparator<Integer>(x -> 1L, x -> 2L);
        assertThat(shadow.quote(5)).isEqualTo(1L);
        assertThat(shadow.mismatches()).hasSize(1);
    }
}
