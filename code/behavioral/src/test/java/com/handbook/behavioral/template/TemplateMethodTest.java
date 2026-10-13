package com.handbook.behavioral.template;

import com.handbook.behavioral.template.admission.Admissions;
import com.handbook.behavioral.template.importer.CsvCustomerImporter;
import com.handbook.behavioral.template.payment.Flows;
import com.handbook.behavioral.template.payment.PaymentFlow;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TemplateMethodTest {

    @Test
    void importerSkipsHeaderAndCountsRejections() {
        String result = new CsvCustomerImporter().run(List.of("h", "1,a,a@x", "2,b,bad"));
        assertThat(result).endsWith("1 loaded, 1 rejected");
    }

    @Test
    void templateValidatesForEveryMethod() {
        PaymentFlow upi = new Flows.UpiFlow();
        assertThatThrownBy(() -> upi.process("O", 0)).hasMessageContaining("positive");
    }

    @Test
    void cardUsesStrongAuthOnlyAboveThreshold() {
        assertThat(new Flows.CardFlow().process("O", 100)).isEqualTo("CAP-AUTH-O");
    }

    @Test
    void emergencySkipsPreAuthAndDeposit() {
        List<String> steps = new Admissions.Emergency().admit("P");
        assertThat(steps).noneMatch(s -> s.contains("pre-authorization"));
        assertThat(steps).anyMatch(s -> s.contains("deposit deferred"));
        assertThat(new Admissions.Planned().admit("P")).anyMatch(s -> s.contains("pre-authorization"));
    }
}
