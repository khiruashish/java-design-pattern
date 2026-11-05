package com.handbook.behavioral.state;

import com.handbook.behavioral.state.approval.DocumentStatus;
import com.handbook.behavioral.state.order.Order;
import com.handbook.behavioral.state.trip.Trip;
import com.handbook.behavioral.state.trip.Trip.Event;
import com.handbook.behavioral.state.trip.Trip.State;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StateTest {

    @Test
    void orderCannotBeCancelledOnceDispatched() {
        Order o = new Order("X");
        o.pack();
        o.dispatch();
        o.cancel();
        assertThat(o.status()).isEqualTo("OUT_FOR_DELIVERY");
    }

    @Test
    void cancellationFeeDependsOnState() {
        State assigned = Trip.next(new State.Searching(), new Event.Assign("d"));
        assertThat(Trip.next(assigned, new Event.RiderCancels())).isEqualTo(new State.Cancelled("rider", 25));
        State waited = Trip.next(Trip.next(assigned, new Event.Arrive()), new Event.Wait(6));
        assertThat(Trip.next(waited, new Event.RiderCancels())).isEqualTo(new State.Cancelled("rider", 75));
    }

    @Test
    void finishedTripsRejectEvents() {
        State done = new State.Completed("d", 100);
        assertThatThrownBy(() -> Trip.next(done, new Event.Arrive())).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void enumWorkflow() {
        assertThat(DocumentStatus.DRAFT.submit().approve().publish()).isEqualTo(DocumentStatus.PUBLISHED);
        assertThatThrownBy(DocumentStatus.DRAFT::publish).hasMessage("Cannot publish: document is DRAFT");
    }
}
