package com.handbook.structural.adapter;

import com.handbook.structural.adapter.healthcare.PulseOximeterAdapter;
import com.handbook.structural.adapter.healthcare.ThermometerAdapter;
import com.handbook.structural.adapter.healthcare.VendorThermometer;
import com.handbook.structural.adapter.legacy.EnumerationIterator;
import com.handbook.structural.adapter.logistics.QuickShipAdapter;
import com.handbook.structural.adapter.logistics.QuickShipApi;
import com.handbook.structural.adapter.logistics.TrackingProvider.Status;
import com.handbook.structural.adapter.payments.LegacyBankAdapter;
import com.handbook.structural.adapter.payments.LegacyBankGateway;
import com.handbook.structural.adapter.healthcare.LegacyPulseOximeter;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AdapterTest {

    @Test
    void bankAdapterConvertsRupeesToPaiseAndCodesToResults() {
        var captured = new String[1];
        LegacyBankGateway spy = new LegacyBankGateway() {
            @Override
            public int doTxn(String ref, String paise, String currency) {
                captured[0] = paise + "/" + currency;
                return super.doTxn(ref, paise, currency);
            }
        };
        var result = new LegacyBankAdapter(spy).charge("O1", new BigDecimal("12.5"));
        assertThat(captured[0]).isEqualTo("1250/356");
        assertThat(result.approved()).isTrue();
    }

    @Test
    void bankAdapterRejectsFractionsOfAPaisa() {
        assertThatThrownBy(() -> new LegacyBankAdapter(new LegacyBankGateway()).charge("O1", new BigDecimal("1.005")))
                .isInstanceOf(ArithmeticException.class);
    }

    @Test
    void quickShipStageIsMappedAndUnknownStageFailsLoudly() {
        assertThat(new QuickShipAdapter(new QuickShipApi()).track("QS-1").status()).isEqualTo(Status.IN_TRANSIT);
        QuickShipApi weird = new QuickShipApi() {
            @Override
            public Map<String, Object> fetch(long no) { return Map.of("stage", 9, "hub", "X"); }
        };
        assertThatThrownBy(() -> new QuickShipAdapter(weird).track("QS-1")).hasMessageContaining("stage 9");
    }

    @Test
    void sensorsAreNormalised() {
        assertThat(new ThermometerAdapter(new VendorThermometer()).read().value()).isEqualTo(38.5);
        VendorThermometer normal = new VendorThermometer() {
            @Override
            public int getTempF10() { return 986; }
        };
        assertThat(new ThermometerAdapter(normal).read().value()).isEqualTo(37.0);
        LegacyPulseOximeter corrupt = new LegacyPulseOximeter() {
            @Override
            public String readFrame() { return "$OX,ID=OX-7,SP"; }
        };
        assertThatThrownBy(() -> new PulseOximeterAdapter(corrupt).read()).hasMessageContaining("Corrupt");
    }

    @Test
    void enumerationAdapterIteratesAndRefusesRemove() {
        var it = new EnumerationIterator<>(Collections.enumeration(List.of("a", "b")));
        assertThat(it.next()).isEqualTo("a");
        assertThatThrownBy(it::remove).isInstanceOf(UnsupportedOperationException.class);
    }
}
