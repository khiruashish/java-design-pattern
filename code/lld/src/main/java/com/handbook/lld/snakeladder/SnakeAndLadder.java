package com.handbook.lld.snakeladder;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;

/**
 * Snake and ladder LLD. Patterns: Builder (board configuration with validation), Strategy (dice,
 * so tests can use a fixed sequence), and a queue of players for turns.
 */
public final class SnakeAndLadder {

    public static final class Board {
        final int size;
        final Map<Integer, Integer> jumps; // snake heads and ladder bottoms → destination

        private Board(int size, Map<Integer, Integer> jumps) {
            this.size = size;
            this.jumps = Map.copyOf(jumps);
        }

        public static Builder builder(int size) {
            return new Builder(size);
        }

        public static final class Builder {
            private final int size;
            private final Map<Integer, Integer> jumps = new HashMap<>();

            Builder(int size) {
                this.size = size;
            }

            public Builder snake(int head, int tail) {
                if (tail >= head) {
                    throw new IllegalArgumentException("snake must go down: " + head + "->" + tail);
                }
                return jump(head, tail);
            }

            public Builder ladder(int bottom, int top) {
                if (top <= bottom) {
                    throw new IllegalArgumentException("ladder must go up: " + bottom + "->" + top);
                }
                return jump(bottom, top);
            }

            private Builder jump(int from, int to) {
                if (from <= 1 || from >= size || to < 1 || to > size || jumps.containsKey(from)) {
                    throw new IllegalArgumentException("invalid jump " + from + "->" + to);
                }
                jumps.put(from, to);
                return this;
            }

            public Board build() {
                return new Board(size, jumps);
            }
        }
    }

    private final Board board;
    private final IntSupplier dice;
    private final Deque<String> turns;
    private final Map<String, Integer> positions = new HashMap<>();

    public SnakeAndLadder(Board board, IntSupplier dice, List<String> players) {
        this.board = board;
        this.dice = dice;
        this.turns = new ArrayDeque<>(players);
        players.forEach(p -> positions.put(p, 0));
    }

    /** Plays one turn and returns a description; ends with "WINS" when someone reaches the last square. */
    public String playTurn() {
        String player = turns.poll();
        int roll = dice.getAsInt();
        int from = positions.get(player);
        int to = from + roll;
        String note = "";
        if (to > board.size) {
            to = from;
            note = " (needs exact roll)";
        } else if (board.jumps.containsKey(to)) {
            int dest = board.jumps.get(to);
            note = dest > to ? " ladder to " + dest : " snake to " + dest;
            to = dest;
        }
        positions.put(player, to);
        turns.addLast(player);
        return String.format("%-5s rolls %d: %2d -> %2d%s%s", player, roll, from, to, note,
                to == board.size ? "  WINS" : "");
    }
}
