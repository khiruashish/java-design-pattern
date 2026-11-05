package com.handbook.behavioral.interpreter;

import com.handbook.behavioral.interpreter.billing.FormulaParser;
import com.handbook.behavioral.interpreter.hours.OpeningHours;
import com.handbook.behavioral.interpreter.promo.RuleParser;
import com.handbook.behavioral.interpreter.roman.Roman;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InterpreterTest {

    @Test
    void andBindsTighterThanOr() {
        var rule = RuleParser.parse("a == 'x' OR b == 'y' AND c == 'z'");
        assertThat(rule.matches(Map.of("a", "x", "b", "n", "c", "n"))).isTrue();
        assertThat(rule.toString()).isEqualTo("(a == 'x' OR (b == 'y' AND c == 'z'))");
    }

    @Test
    void missingFieldsDoNotMatch() {
        assertThat(RuleParser.parse("total > 1").matches(Map.of())).isFalse();
    }

    @Test
    void formulaRespectsPrecedenceAndFunctions() {
        assertThat(FormulaParser.parse("2 + 3 * 4").eval(Map.of())).isEqualTo(14.0);
        assertThat(FormulaParser.parse("(2 + 3) * 4").eval(Map.of())).isEqualTo(20.0);
        assertThat(FormulaParser.parse("max(x - 10, 0)").eval(Map.of("x", 4.0))).isEqualTo(0.0);
        assertThatThrownBy(() -> FormulaParser.parse("y + 1").eval(Map.of())).hasMessageContaining("Unknown variable");
    }

    @Test
    void openingHoursWrapAroundTheWeek() {
        var h = OpeningHours.parse("SAT-MON 08:00-10:00");
        assertThat(h.isOpen(DayOfWeek.SUNDAY, LocalTime.of(9, 0))).isTrue();
        assertThat(h.isOpen(DayOfWeek.TUESDAY, LocalTime.of(9, 0))).isFalse();
        assertThat(h.isOpen(DayOfWeek.MONDAY, LocalTime.of(10, 0))).isFalse(); // end is exclusive
    }

    @Test
    void romanNumerals() {
        assertThat(Roman.interpret("XIV")).isEqualTo(14);
        assertThatThrownBy(() -> Roman.interpret("ABC")).isInstanceOf(IllegalArgumentException.class);
    }
}
