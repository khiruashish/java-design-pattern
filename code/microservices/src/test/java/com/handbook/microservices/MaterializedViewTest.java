package com.handbook.microservices;

import com.handbook.microservices.materializedview.balances.BalanceView;
import com.handbook.microservices.materializedview.balances.BalanceView.LedgerEntry;
import com.handbook.microservices.materializedview.sales.StoreSalesView;
import com.handbook.microservices.materializedview.tracking.TrackingView;
import com.handbook.microservices.materializedview.tracking.TrackingView.EtaUpdated;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MaterializedViewTest {

    @Test
    void summaryReflectsDataOnlyAfterRefresh() {
        StoreSalesView v = new StoreSalesView();
        v.insertLines("S", "2026-01-01", 2, 100);
        assertThat(v.dashboard("S", "2026-01-01")).isEqualTo("no data yet");
        v.refresh();
        assertThat(v.dashboard("S", "2026-01-01")).isEqualTo("2 orders, INR 2");
    }

    @Test
    void duplicateEventsDoNotDoubleCount() {
        BalanceView v = new BalanceView();
        v.apply(new LedgerEntry("e1", "A", 100));
        v.apply(new LedgerEntry("e1", "A", 100));
        assertThat(v.balance("A")).isEqualTo(100);
    }

    @Test
    void olderEventsFromTheSameSourceAreIgnored() {
        TrackingView v = new TrackingView();
        v.on(new EtaUpdated("X", "10:00", 2));
        v.on(new EtaUpdated("X", "09:00", 1));
        assertThat(v.page("X").eta()).isEqualTo("10:00");
    }
}
