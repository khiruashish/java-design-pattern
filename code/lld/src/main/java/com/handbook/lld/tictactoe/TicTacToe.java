package com.handbook.lld.tictactoe;

/**
 * Tic-tac-toe on an n×n board with O(1) win detection: keep a running sum per row, column and
 * diagonal (+1 for X, -1 for O). A line wins when its sum reaches ±n.
 */
public final class TicTacToe {

    public enum Result { IN_PROGRESS, X_WINS, O_WINS, DRAW }

    private final int n;
    private final char[][] cells;
    private final int[] rows;
    private final int[] cols;
    private int diagonal;
    private int antiDiagonal;
    private int moves;
    private char turn = 'X';
    private Result result = Result.IN_PROGRESS;

    public TicTacToe(int n) {
        this.n = n;
        this.cells = new char[n][n];
        this.rows = new int[n];
        this.cols = new int[n];
    }

    public Result move(int r, int c) {
        if (result != Result.IN_PROGRESS) {
            throw new IllegalStateException("game over: " + result);
        }
        if (r < 0 || c < 0 || r >= n || c >= n || cells[r][c] != 0) {
            throw new IllegalArgumentException("illegal move " + r + "," + c);
        }
        cells[r][c] = turn;
        int delta = turn == 'X' ? 1 : -1;
        rows[r] += delta;
        cols[c] += delta;
        if (r == c) {
            diagonal += delta;
        }
        if (r + c == n - 1) {
            antiDiagonal += delta;
        }
        moves++;
        if (Math.abs(rows[r]) == n || Math.abs(cols[c]) == n || Math.abs(diagonal) == n || Math.abs(antiDiagonal) == n) {
            result = turn == 'X' ? Result.X_WINS : Result.O_WINS;
        } else if (moves == n * n) {
            result = Result.DRAW;
        }
        turn = turn == 'X' ? 'O' : 'X';
        return result;
    }

    public String board() {
        StringBuilder sb = new StringBuilder();
        for (char[] row : cells) {
            for (char cell : row) {
                sb.append(cell == 0 ? '.' : cell);
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
