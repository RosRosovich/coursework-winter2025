package com.tictactoe.players;

import com.tictactoe.core.Board;

import java.util.Objects;
import java.util.Scanner;

public abstract class Player {
    private final String name;
    private final char symbol;

    protected Player(final String name, final char symbol) {
        validateName(name);
        validateSymbol(symbol);

        this.name = name.trim();
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public char getSymbol() {
        return symbol;
    }

    public abstract boolean makeMove(Board board, Scanner scanner);

    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        final Player player = (Player) object;
        return symbol == player.symbol && Objects.equals(name, player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, symbol);
    }

    @Override
    public String toString() {
        return String.format("Player{name='%s', symbol=%s}", name, symbol);
    }

    private void validateName(final String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be null or empty");
        }
    }

    private void validateSymbol(final char symbol) {
        if (symbol != 'X' && symbol != 'O') {
            throw new IllegalArgumentException("Player symbol must be 'X' or 'O'");
        }
    }
}