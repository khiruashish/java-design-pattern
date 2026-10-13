package com.handbook.lld.snakeladder;

import com.handbook.lld.snakeladder.SnakeAndLadder.Board;

import java.util.List;
import java.util.Random;

public class SnakeAndLadderDemo {

    public static void main(String[] args) {
        Board board = Board.builder(30)
                .ladder(3, 22).ladder(5, 8).ladder(11, 26).ladder(20, 29)
                .snake(27, 1).snake(21, 9).snake(17, 4).snake(19, 7)
                .build();
        Random seeded = new Random(19);
        var game = new SnakeAndLadder(board, () -> 1 + seeded.nextInt(6), List.of("Asha", "Ravi"));

        System.out.println("=== LLD: Snake and ladder (30 squares, seeded dice) ===");
        for (int turn = 0; turn < 40; turn++) {
            String line = game.playTurn();
            System.out.println(line);
            if (line.endsWith("WINS")) {
                break;
            }
        }
        try {
            Board.builder(30).snake(5, 12);
        } catch (IllegalArgumentException e) {
            System.out.println("Builder validation: " + e.getMessage());
        }
    }
}
