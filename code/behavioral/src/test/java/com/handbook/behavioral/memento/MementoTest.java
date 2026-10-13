package com.handbook.behavioral.memento;

import com.handbook.behavioral.memento.booking.BookingWizard;
import com.handbook.behavioral.memento.canvas.Canvas;
import com.handbook.behavioral.memento.cart.Cart;
import com.handbook.behavioral.memento.history.BoundedHistory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MementoTest {

    @Test
    void snapshotIsNotAffectedByLaterChanges() {
        Canvas c = new Canvas();
        c.add(new Canvas.Shape("a", 0, 0, "red"));
        Canvas.Snapshot s = c.save("v1");
        c.recolorAll("blue");
        c.restore(s);
        assertThat(c.toString()).contains("a(red)");
    }

    @Test
    void cartRollbackRestoresItemsAndCoupon() {
        Cart cart = new Cart();
        cart.add("A", 1);
        var cp = cart.checkpoint();
        cart.applyCoupon("X");
        cart.clear();
        cart.rollback(cp);
        assertThat(cart).hasToString("{A=1}");
    }

    @Test
    void draftRoundTripsThroughToken() {
        BookingWizard w = new BookingWizard();
        w.answer("city", "Goa");
        assertThat(BookingWizard.resume(w.saveDraft()).toString()).isEqualTo(w.toString());
    }

    @Test
    void boundedHistoryDropsOldest() {
        BoundedHistory<Integer> h = new BoundedHistory<>(2);
        h.push(1);
        h.push(2);
        h.push(3);
        assertThat(h.pop()).contains(3);
        assertThat(h.pop()).contains(2);
        assertThat(h.pop()).isEmpty();
    }
}
