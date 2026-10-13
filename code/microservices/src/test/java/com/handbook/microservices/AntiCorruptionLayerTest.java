package com.handbook.microservices;

import com.handbook.microservices.acl.erp.ErpProductTranslator;
import com.handbook.microservices.acl.erp.ErpProductTranslator.Unit;
import com.handbook.microservices.acl.logistics.CarrierStatusTranslator;
import com.handbook.microservices.acl.logistics.CarrierStatusTranslator.CarrierEvent;
import com.handbook.microservices.acl.logistics.CarrierStatusTranslator.ShipmentStatus;
import com.handbook.microservices.acl.outbound.LegacyOrderExporter;
import com.handbook.microservices.acl.outbound.LegacyOrderExporter.OrderLine;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AntiCorruptionLayerTest {

    @Test
    void erpFieldsAreTranslatedIntoDomainTypes() {
        var acl = new ErpProductTranslator(n -> Map.of("MAT_NO", n, "MAT_DESC", "RICE", "BASE_UOM", "ST",
                "PRICE", "1.000,50", "DEL_FLAG", ""));
        var p = acl.product("SKU-7").orElseThrow();
        assertThat(p.sku()).isEqualTo("SKU-7");
        assertThat(p.unit()).isEqualTo(Unit.PIECE);
        assertThat(p.pricePaise()).isEqualTo(100_050);
        assertThat(p.active()).isTrue();
    }

    @Test
    void unknownCarrierCodesBecomeUnknown() {
        var update = new CarrierStatusTranslator().translate(new CarrierEvent("BlueKite", "1", "42"));
        assertThat(update.status()).isEqualTo(ShipmentStatus.UNKNOWN);
    }

    @Test
    void fixedWidthRecordsHaveExactLength() {
        String r = LegacyOrderExporter.toRecord(new OrderLine("O-1", "Élodie", 3, LocalDate.of(2026, 1, 2)));
        assertThat(r).hasSize(41).contains("ELODIE").endsWith("00003260102");
    }
}
