package com.handbook.enterprise.valueobject;

import com.handbook.enterprise.valueobject.grocery.Quantity;
import com.handbook.enterprise.valueobject.healthcare.StayPeriod;
import com.handbook.enterprise.valueobject.telecom.PhoneNumber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static com.handbook.enterprise.valueobject.grocery.Quantity.Unit.GRAM;
import static com.handbook.enterprise.valueobject.grocery.Quantity.Unit.KILOGRAM;
import static com.handbook.enterprise.valueobject.grocery.Quantity.Unit.PIECE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValueObjectTest {

    @Test
    void quantitiesAreEqualByValueAcrossUnits() {
        assertThat(Quantity.of(2, KILOGRAM)).isEqualTo(Quantity.of(2000, GRAM)).hasSameHashCodeAs(Quantity.of(2000, GRAM));
        assertThatThrownBy(() -> Quantity.of(1, KILOGRAM).plus(Quantity.of(1, PIECE))).isInstanceOf(IllegalArgumentException.class);
        assertThat(Quantity.of(250, GRAM).priceFor(6_000)).isEqualTo(1_500);
    }

    @Test
    void halfOpenStaysDoNotOverlapOnTheSameDay() {
        StayPeriod a = new StayPeriod(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 3));
        assertThat(a.overlaps(new StayPeriod(LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 4)))).isFalse();
        assertThat(a.overlaps(new StayPeriod(LocalDate.of(2026, 1, 2), LocalDate.of(2026, 1, 4)))).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"+91 98450 12345", "098450-12345", "9845012345", "919845012345"})
    void everySpellingNormalisesToTheSameNumber(String raw) {
        assertThat(PhoneNumber.parse(raw)).isEqualTo(new PhoneNumber("+919845012345"));
    }
}
