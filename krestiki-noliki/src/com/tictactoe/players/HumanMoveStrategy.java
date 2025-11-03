package com.tictactoe.players;

import com.tictactoe.core.Board;
import com.tictactoe.core.GameConstants;

import java.util.Objects;
import java.util.Scanner;

public record HumanMoveStrategy(Scanner scanner) implements MoveStrategy {
    public HumanMoveStrategy {
        Objects.requireNonNull(scanner, "Scanner cannot be null");
    }

    @Override
    public boolean makeMove(final Board board, final Player player) {
        System.out.print("Введите ход (строка столбец, 1-3 через пробел), например: 2 3 : ");
        final String line = scanner.nextLine().trim();
        final String[] parts = line.split("\\s+");

        if (parts.length != 2) {
            System.out.println("Нужно ввести два числа через пробел (например: 1 3).");
            return false;
        }

        try {
            final int row = Integer.parseInt(parts[0]);
            final int column = Integer.parseInt(parts[1]);

            if (isInvalidCoordinate(row) || isInvalidCoordinate(column)) {
                System.out.println("Координаты должны быть в диапазоне " + GameConstants.MIN_COORDINATE
                        + ".." + GameConstants.MAX_COORDINATE + ".");
                return false;
            }

            final int rowIndex = row - 1;
            final int columnIndex = column - 1;

            if (!board.isCellEmpty(rowIndex, columnIndex)) {
                System.out.println("Клетка " + row + " " + column + " уже занята.");
                return false;
            }

            return board.setCell(rowIndex, columnIndex, player.getSymbol());

        } catch (NumberFormatException exception) {
            System.out.println("Нужно вводить числа, например: 2 2");
            return false;
        }
    }

    private boolean isInvalidCoordinate(final int coordinate) {
        return coordinate < GameConstants.MIN_COORDINATE || coordinate > GameConstants.MAX_COORDINATE;
    }
}