package com.handbook.enterprise.valueobject.grocery;

import java.util.Objects;

/** A quantity of something, with its unit. Equal by value; normalised to the smallest unit. */
public record Quantity(long amount, Unit unit) {

    public enum Unit {
        GRAM(Dimension.MASS, 1), KILOGRAM(Dimension.MASS, 1000), PIECE(Dimension.COUNT, 1), DOZEN(Dimension.COUNT, 12);

        final Dimension dimension;
        final long factor;

        Unit(Dimension dimension, long factor) {
            this.dimension = dimension;
            this.factor = factor;
        }
    }

    enum Dimension { MASS, COUNT }

    public Quantity { // (1)!
        Objects.requireNonNull(unit, "unit");
        if (amount < 0) {
            throw new IllegalArgumentException("quantity cannot be negative: " + amount);
        }
        long base = Math.multiplyExact(amount, unit.factor);
        amount = base;
        unit = unit.dimension == Dimension.MASS ? Unit.GRAM : Unit.PIECE;
    }

    public static Quantity of(long amount, Unit unit) {
        return new Quantity(amount, unit);
    }

    public Quantity plus(Quantity other) { // (2)!
        if (unit.dimension != other.unit.dimension) {
            throw new IllegalArgumentException("cannot add " + other + " to " + this);
        }
        return new Quantity(amount + other.amount, unit);
    }

    /** Price for this quantity, given a price per kg or per piece, in paise (rounded half-up). */
    public long priceFor(long paisePerKgOrPiece) {
        long divisor = unit == Unit.GRAM ? 1000 : 1;
        return (amount * paisePerKgOrPiece + divisor / 2) / divisor;
    }

    @Override
    public String toString() {
        if (unit == Unit.GRAM) {
            return amount % 1000 == 0 ? amount / 1000 + " kg" : amount + " g";
        }
        return amount + " pc";
    }
}
