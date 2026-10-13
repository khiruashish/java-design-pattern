package com.handbook.behavioral.nullobject;

import com.handbook.behavioral.nullobject.audit.AuditSink;
import com.handbook.behavioral.nullobject.audit.ProjectService;
import com.handbook.behavioral.nullobject.coupon.Coupon;
import com.handbook.behavioral.nullobject.viewer.Viewer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NullObjectTest {

    @Test
    void unknownAndMissingCodesBecomeNone() {
        assertThat(Coupon.lookup(null)).isSameAs(Coupon.NONE);
        assertThat(Coupon.lookup("NOPE")).isSameAs(Coupon.NONE);
        assertThat(Coupon.NONE.discountInr(1_000)).isZero();
    }

    @Test
    void businessLogicWorksWithDisabledAudit() {
        ProjectService s = new ProjectService(AuditSink.DISABLED);
        s.createProject("u", "p");
        assertThat(s.projects()).isEqualTo(1);
    }

    @Test
    void guestIsSafeToCallEverything() {
        Viewer.GUEST.recordWatched("x");
        assertThat(Viewer.GUEST.canDownload()).isFalse();
        assertThat(Viewer.GUEST.recommendations()).isNotEmpty();
    }
}
