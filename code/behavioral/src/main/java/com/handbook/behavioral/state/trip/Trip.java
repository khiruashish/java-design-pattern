package com.handbook.behavioral.state.trip;

/**
 * Functional state machine: states and events are immutable sealed types;
 * a pure function computes the next state. Easy to test, persist and replay.
 */
public final class Trip {

    private Trip() {
    }

    public sealed interface State {
        record Searching() implements State { }
        record DriverAssigned(String driver) implements State { }
        record DriverArrived(String driver, int waitedMin) implements State { }
        record InProgress(String driver) implements State { }
        record Completed(String driver, int fareInr) implements State { }
        record Cancelled(String by, int feeInr) implements State { }
    }

    public sealed interface Event {
        record Assign(String driver) implements Event { }
        record Arrive() implements Event { }
        record Wait(int minutes) implements Event { }
        record Start() implements Event { }
        record Finish(int km) implements Event { }
        record RiderCancels() implements Event { }
    }

    public static State next(State state, Event event) { // (1)!
        return switch (state) {
            case State.Searching s -> switch (event) {
                case Event.Assign a -> new State.DriverAssigned(a.driver());
                case Event.RiderCancels c -> new State.Cancelled("rider", 0);
                default -> invalid(state, event);
            };
            case State.DriverAssigned s -> switch (event) {
                case Event.Arrive a -> new State.DriverArrived(s.driver(), 0);
                case Event.RiderCancels c -> new State.Cancelled("rider", 25); // driver already on the way
                default -> invalid(state, event);
            };
            case State.DriverArrived s -> switch (event) {
                case Event.Wait w -> new State.DriverArrived(s.driver(), s.waitedMin() + w.minutes());
                case Event.Start st -> new State.InProgress(s.driver());
                case Event.RiderCancels c -> new State.Cancelled("rider", s.waitedMin() > 5 ? 75 : 50);
                default -> invalid(state, event);
            };
            case State.InProgress s -> switch (event) {
                case Event.Finish f -> new State.Completed(s.driver(), 50 + 14 * f.km());
                default -> invalid(state, event);
            };
            case State.Completed s -> invalid(state, event);
            case State.Cancelled s -> invalid(state, event);
        };
    }

    private static State invalid(State state, Event event) {
        throw new IllegalStateException(event.getClass().getSimpleName() + " not allowed in "
                + state.getClass().getSimpleName());
    }
}
