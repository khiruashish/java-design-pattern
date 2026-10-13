package com.handbook.microservices.saga.travel;

import com.handbook.microservices.saga.core.SagaOrchestrator;
import com.handbook.microservices.saga.core.SagaOrchestrator.Outcome;
import com.handbook.microservices.saga.core.SagaOrchestrator.Step;

import java.util.List;
import java.util.function.Consumer;

/**
 * Flight and hotel holds can be cancelled. Charging the card is the pivot: once it succeeds the
 * trip is sold, so the cab and confirmation e-mail are retried rather than undoing the whole trip.
 */
public final class TripBookingSaga {

    public interface Partners {
        void holdFlight(String trip);

        void cancelFlight(String trip);

        void holdHotel(String trip);

        void cancelHotel(String trip);

        void chargeCard(String trip);

        void bookAirportCab(String trip);

        void sendItinerary(String trip);
    }

    private final Partners p;

    public TripBookingSaga(Partners partners) {
        this.p = partners;
    }

    public Outcome book(String trip) {
        return new SagaOrchestrator(3).run(trip, List.of(
                Step.compensatable("hold flight", run(p::holdFlight, trip), run(p::cancelFlight, trip)),
                Step.compensatable("hold hotel", run(p::holdHotel, trip), run(p::cancelHotel, trip)),
                Step.pivot("charge card", run(p::chargeCard, trip)), // (1)!
                Step.retriable("book airport cab", run(p::bookAirportCab, trip)), // (2)!
                Step.retriable("send itinerary", run(p::sendItinerary, trip))));
    }

    private static Runnable run(Consumer<String> call, String trip) {
        return () -> call.accept(trip);
    }
}
