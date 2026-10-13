package com.handbook.lld.tictactoe;

public class TicTacToeDemo {

    public static void main(String[] args) {
        System.out.println("=== LLD: Tic-tac-toe with O(1) win check ===");
        var game = new TicTacToe(3);
        int[][] moves = {{1, 1}, {0, 0}, {0, 2}, {2, 0}, {1, 0}, {1, 2}, {2, 2}, {0, 1}, {2, 1}};
        TicTacToe.Result result = null;
        for (int[] m : moves) {
            result = game.move(m[0], m[1]);
        }
        System.out.print(game.board());
        System.out.println("result: " + result);

        var quick = new TicTacToe(3);
        quick.move(0, 0);
        quick.move(1, 0);
        quick.move(1, 1);
        quick.move(2, 0);
        System.out.println("X plays 2,2 -> " + quick.move(2, 2) + " (main diagonal)");
        try {
            quick.move(0, 1);
        } catch (IllegalStateException e) {
            System.out.println("next move -> " + e.getMessage());
        }
    }
}
