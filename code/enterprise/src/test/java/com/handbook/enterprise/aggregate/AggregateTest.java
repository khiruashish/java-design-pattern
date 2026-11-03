package com.handbook.enterprise.aggregate;

import com.handbook.enterprise.aggregate.banking.CardAccount;
import com.handbook.enterprise.aggregate.cinema.Show;
import com.handbook.enterprise.aggregate.cinema.Show.CustomerId;
import com.handbook.enterprise.aggregate.cinema.Show.ShowId;
import com.handbook.enterprise.aggregate.grocery.Order;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AggregateTest {

    @Test
    void orderEnforcesPerSkuLimitAcrossAdds() {
        Order o = new Order("O", "C");
        o.add("A", 100, 3);
        assertThatThrownBy(() -> o.add("A", 100, 2)).hasMessageContaining("max 4");
        assertThat(o.lines()).singleElement().extracting(Order.Line::qty).isEqualTo(3);
    }

    @Test
    void linesCannotBeModifiedFromOutside() {
        Order o = new Order("O", "C");
        o.add("A", 100, 1);
        assertThatThrownBy(() -> o.lines().clear()).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void cardNeverExceedsLimitIncludingHolds() {
        CardAccount card = new CardAccount("C", 1_000);
        assertThat(card.authorise("a", 700)).isTrue();
        assertThat(card.authorise("b", 400)).isFalse();
        card.capture("a", 500);
        assertThat(card.authorise("b", 400)).isTrue();
        assertThat(card.available()).isEqualTo(100);
    }

    @Test
    void bookingIsAllOrNothing() {
        Show show = new Show(new ShowId("S"), 10);
        show.book(new CustomerId("1"), "A1");
        assertThatThrownBy(() -> show.book(new CustomerId("2"), "A2", "A1")).isInstanceOf(IllegalStateException.class);
        assertThat(show.bookedSeats()).containsOnlyKeys("A1");
    }
}
