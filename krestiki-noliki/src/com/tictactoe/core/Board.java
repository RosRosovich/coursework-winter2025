package com.tictactoe.core;

import com.tictactoe.players.BotPlayer;
import com.tictactoe.players.Player;
import com.tictactoe.storage.ColorManager;
import com.tictactoe.storage.ConsoleColors;

import java.util.Objects;

public class Board {
    public static final char EMPTY_CELL = GameConstants.EMPTY_CELL;
    private static final int BOARD_SIZE = GameConstants.BOARD_SIZE;
    private final char[][] board;

    public Board() {
        board = new char[BOARD_SIZE][BOARD_SIZE];
        clear();
    }

    public void clear() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = EMPTY_CELL;
            }
        }
    }

    public boolean setCell(final int row, final int col, final char symbol) {
        if (!isValidCoordinate(row, col)) {
            return false;
        }
        if (board[row][col] != EMPTY_CELL) {
            return false;
        }
        board[row][col] = symbol;
        return true;
    }

    public void replaceCell(final int row, final int col, final char symbol) {
        if (isValidCoordinate(row, col)) {
            board[row][col] = symbol;
        }
    }

    public char getCell(final int row, final int col) {
        if (!isValidCoordinate(row, col)) {
            return EMPTY_CELL;
        }
        return board[row][col];
    }

    public boolean isCellEmpty(final int row, final int col) {
        return getCell(row, col) == EMPTY_CELL;
    }

    public char checkWin() {
        final char rowWinner = checkRows();
        if (rowWinner != EMPTY_CELL) {
            return rowWinner;
        }

        final char columnWinner = checkColumns();
        if (columnWinner != EMPTY_CELL) {
            return columnWinner;
        }

        return checkDiagonals();
    }

    public boolean isFull() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == EMPTY_CELL) {
                    return false;
                }
            }
        }
        return true;
    }

    public void printBoard(final Player player1, final Player player2) {
        Objects.requireNonNull(player1, "Player1 cannot be null");
        Objects.requireNonNull(player2, "Player2 cannot be null");

        System.out.println("\n      1      2      3");
        for (int i = 0; i < BOARD_SIZE; i++) {
            printBoardRow(i, player1, player2);
            if (i < BOARD_SIZE - 1) {
                System.out.println("  --------------------");
            }
        }
    }

    private char checkRows() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i][0] != EMPTY_CELL && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                return board[i][0];
            }
        }
        return EMPTY_CELL;
    }

    private char checkColumns() {
        for (int j = 0; j < BOARD_SIZE; j++) {
            if (board[0][j] != EMPTY_CELL && board[0][j] == board[1][j] && board[1][j] == board[2][j]) {
                return board[0][j];
            }
        }
        return EMPTY_CELL;
    }

    private char checkDiagonals() {
        if (board[0][0] != EMPTY_CELL && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            return board[0][0];
        }

        if (board[0][2] != EMPTY_CELL && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            return board[0][2];
        }

        return EMPTY_CELL;
    }

    private void printBoardRow(final int row, final Player player1, final Player player2) {
        System.out.print((row + 1) + " ");
        for (int j = 0; j < BOARD_SIZE; j++) {
            final char cell = board[row][j];
            final String toPrint = formatCell(cell, player1, player2);

            System.out.print("   " + toPrint + "   ");
            if (j < BOARD_SIZE - 1) {
                System.out.print("|");
            }
        }
        System.out.println();
    }

    private String formatCell(final char cell, final Player player1, final Player player2) {
        Objects.requireNonNull(player1, "Player1 cannot be null");
        Objects.requireNonNull(player2, "Player2 cannot be null");

        if (cell == GameConstants.PLAYER_X || cell == GameConstants.PLAYER_O) {
            final Player owner = (player1.getSymbol() == cell) ? player1 : player2;
            return getCellDisplay(owner, cell);
        } else {
            return " ";
        }
    }

    private String getCellDisplay(final Player owner, final char cell) {
        Objects.requireNonNull(owner, "Owner cannot be null");

        if (owner instanceof BotPlayer bot && bot.isHardMode()) {
            return ConsoleColors.RED + cell + ConsoleColors.RESET;
        } else {
            final String color = ColorManager.getColorCode(ColorManager.getPlayerColor(owner.getName()));
            return color + cell + ConsoleColors.RESET;
        }
    }

    private boolean isValidCoordinate(final int row, final int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }
}