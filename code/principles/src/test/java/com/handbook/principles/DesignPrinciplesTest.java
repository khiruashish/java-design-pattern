package com.handbook.principles;

import com.handbook.principles.other.composition.InstrumentedHashSet;
import com.handbook.principles.other.composition.InstrumentedSet;
import com.handbook.principles.other.demeter.Customer;
import com.handbook.principles.other.demeter.Paperboy;
import com.handbook.principles.other.demeter.Wallet;
import com.handbook.principles.other.dry.EmailAddress;
import com.handbook.principles.other.dry.SignupBefore;
import com.handbook.principles.other.failfast.Reservation;
import com.handbook.principles.other.kiss.KissExamples;
import com.handbook.principles.other.telldontask.Thermostat;
import com.handbook.principles.other.yagni.YagniExamples;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DesignPrinciplesTest {

    @Test
    void dry_singleRuleNoDrift() {
        SignupBefore before = new SignupBefore();
        assertThat(before.canRegister("@x")).isNotEqualTo(before.canInvite("@x"));
        assertThat(EmailAddress.isValid("@x")).isFalse();
        assertThat(new EmailAddress(" A@B.com ").value()).isEqualTo("a@b.com");
    }

    @ParameterizedTest
    @ValueSource(ints = {1900, 2000, 2023, 2024, 2100, 2400})
    void kiss_simpleAndCleverAgree(int year) {
        assertThat(KissExamples.isLeapSimple(year)).isEqualTo(KissExamples.isLeapClever(year));
    }

    @Test
    void yagni_simpleConverter() {
        assertThat(new YagniExamples.InrToUsd(120).convertPaise(10_000)).isEqualTo(120);
    }

    @Test
    void composition_fixesDoubleCounting() {
        InstrumentedHashSet<String> inherited = new InstrumentedHashSet<>();
        inherited.addAll(List.of("a", "b", "c"));
        assertThat(inherited.addCount()).isEqualTo(6);

        InstrumentedSet<String> composed = new InstrumentedSet<>(new HashSet<>());
        composed.addAll(List.of("a", "b", "c"));
        assertThat(composed.addCount()).isEqualTo(3);
        assertThat(composed).containsExactlyInAnyOrder("a", "b", "c");
    }

    @Test
    void demeter_customerOwnsPaymentRule() {
        Customer ana = new Customer("Ana", new Wallet(500));
        Paperboy boy = new Paperboy();
        boy.collectAfter(ana, 200);
        assertThat(boy.collected()).isEqualTo(200);
        assertThatThrownBy(() -> boy.collectAfter(ana, 400)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void tellDontAsk_ruleLivesInOneObject() {
        Thermostat t = new Thermostat(21);
        t.onTemperatureReading(20);
        assertThat(t.isHeating()).isFalse();   // within hysteresis band
        t.onTemperatureReading(19);
        assertThat(t.isHeating()).isTrue();
    }

    @Test
    void failFast_rejectsInvalidStateAtConstruction() {
        LocalDate d = LocalDate.of(2025, 1, 10);
        assertThatThrownBy(() -> new Reservation("Ana", d, d.minusDays(1), 2)).hasMessageContaining("must be after");
        assertThatThrownBy(() -> new Reservation("Ana", d, d.plusDays(1), 0)).hasMessageContaining("1..8");
        assertThatThrownBy(() -> new Reservation(null, d, d.plusDays(1), 1)).isInstanceOf(NullPointerException.class);
        assertThat(new Reservation("Ana", d, d.plusDays(2), 1).nights()).isEqualTo(2);
    }
}
