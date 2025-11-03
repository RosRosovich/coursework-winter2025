package com.tictactoe.core;

import java.util.Objects;

public record GameResult(String winner, boolean isDraw) {
    public GameResult {
        if (winner != null && winner.trim().isEmpty()) {
            throw new IllegalArgumentException("Winner cannot be empty string");
        }
    }

    public static GameResult win(String winner) {
        Objects.requireNonNull(winner, "Winner cannot be null");
        return new GameResult(winner, false);
    }

    public static GameResult draw() {
        return new GameResult(null, true);
    }

    public static GameResult none() {
        return new GameResult(null, false);
    }

    public boolean hasWinner() {
        return winner != null;
    }
}