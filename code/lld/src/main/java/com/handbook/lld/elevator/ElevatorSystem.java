package com.handbook.lld.elevator;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Elevator LLD. Patterns: State (each car's direction), Strategy (which car serves a hall call),
 * and the SCAN ("elevator") algorithm: keep moving in one direction while there are stops ahead.
 */
public final class ElevatorSystem {

    public enum Direction { UP, DOWN, IDLE }

    public static final class Car {
        final String id;
        int floor;
        Direction direction = Direction.IDLE;
        final TreeSet<Integer> stops = new TreeSet<>();

        Car(String id, int floor) {
            this.id = id;
            this.floor = floor;
        }

        /** One tick: move one floor toward the next stop in the current direction (SCAN). */
        String step() {
            if (stops.isEmpty()) {
                direction = Direction.IDLE;
                return id + "@" + floor + " idle";
            }
            if (direction == Direction.IDLE) {
                direction = stops.first() >= floor ? Direction.UP : Direction.DOWN;
            }
            if (direction == Direction.UP && stops.ceiling(floor) == null) {
                direction = Direction.DOWN; // nothing above: reverse
            } else if (direction == Direction.DOWN && stops.floor(floor) == null) {
                direction = Direction.UP;
            }
            if (stops.remove(floor)) {
                return id + "@" + floor + " doors open";
            }
            floor += direction == Direction.UP ? 1 : -1;
            if (stops.remove(floor)) {
                return id + "@" + floor + " doors open";
            }
            return id + "@" + floor + " moving " + direction;
        }
    }

    /** Strategy for assigning a hall call to a car. */
    public interface Dispatcher {
        Car choose(List<Car> cars, int floor, Direction wanted);
    }

    /** Prefer an idle car or one already heading toward the floor in the same direction; then nearest. */
    public static Dispatcher nearestSuitable() {
        return (cars, floor, wanted) -> cars.stream()
                .min((a, b) -> Integer.compare(cost(a, floor, wanted), cost(b, floor, wanted)))
                .orElseThrow();
    }

    private static int cost(Car c, int floor, Direction wanted) {
        int distance = Math.abs(c.floor - floor);
        boolean onTheWay = (c.direction == Direction.UP && wanted == Direction.UP && floor >= c.floor)
                || (c.direction == Direction.DOWN && wanted == Direction.DOWN && floor <= c.floor);
        if (c.direction == Direction.IDLE || onTheWay) {
            return distance;
        }
        return distance + 100; // busy in the other direction: last resort
    }

    private final List<Car> cars = new ArrayList<>();
    private final Dispatcher dispatcher;

    public ElevatorSystem(Dispatcher dispatcher, int... startFloors) {
        this.dispatcher = dispatcher;
        for (int i = 0; i < startFloors.length; i++) {
            cars.add(new Car("E" + (i + 1), startFloors[i]));
        }
    }

    public String hallCall(int floor, Direction wanted) {
        Car car = dispatcher.choose(cars, floor, wanted);
        car.stops.add(floor);
        return "hall call " + floor + " " + wanted + " -> " + car.id;
    }

    public void carCall(String carId, int floor) {
        cars.stream().filter(c -> c.id.equals(carId)).findFirst().orElseThrow().stops.add(floor);
    }

    public List<String> tick() {
        return cars.stream().map(Car::step).toList();
    }
}
