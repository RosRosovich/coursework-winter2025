package com.tictactoe.core;

public final class GameConstants {
    public static final int BOARD_SIZE = 3;
    public static final int MIN_COORDINATE = 1;
    public static final int MAX_COORDINATE = 3;
    public static final char EMPTY_CELL = ' ';
    public static final char PLAYER_X = 'X';
    public static final char PLAYER_O = 'O';
    public static final int DICE_SIDES = 6;
    public static final int WINS_REQUIRED_FOR_HARDCORE = 10;
    public static final int STEAL_PROBABILITY_PERCENT = 40;
    public static final int PERCENTAGE_BASE = 100;
    public static final int THINKING_DELAY_MILLIS = 1500;

    private GameConstants() {
        throw new AssertionError("Cannot instantiate utility class");
    }
}