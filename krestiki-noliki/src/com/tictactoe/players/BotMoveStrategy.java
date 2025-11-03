package com.tictactoe.players;

import com.tictactoe.core.Board;
import com.tictactoe.core.GameConstants;
import com.tictactoe.storage.ConsoleColors;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BotMoveStrategy implements MoveStrategy {
    protected final Random random;
    protected final boolean hardMode;

    public BotMoveStrategy(final boolean hardMode) {
        this.random = new Random();
        this.hardMode = hardMode;
    }

    @Override
    public boolean makeMove(final Board board, final Player player) {
        return hardMode ? makeHardcoreMove(board, player) : makeNormalMove(board, player);
    }

    protected boolean makeNormalMove(final Board board, final Player player) {
        final int[] move = getRandomMove(board);
        return executeMove(board, player, move, player.getName() + " походил: ");
    }

    protected boolean makeHardcoreMove(final Board board, final Player player) {
        final char opponentSymbol = getOpponentSymbol(player.getSymbol());

        if (shouldStealCell()) {
            final int[] stealMove = findPlayerCellToSteal(board, opponentSymbol);
            if (stealMove != null) {
                return executeStealMove(board, player, stealMove);
            }
        }

        final int[] randomMove = getRandomMove(board);
        return executeMove(board, player, randomMove, ConsoleColors.RED + player.getName() + " (HARDCORE) походил: " + ConsoleColors.RESET);
    }

    protected boolean executeMove(final Board board, final Player player, final int[] move, final String message) {
        if (move == null) {
            return false;
        }

        final boolean success = board.setCell(move[0], move[1], player.getSymbol());
        if (success) {
            final String moveInfo = (move[0] + 1) + " " + (move[1] + 1);
            System.out.println(message + moveInfo);
        }
        return success;
    }

    protected boolean executeStealMove(final Board board, final Player player, final int[] stealMove) {
        board.replaceCell(stealMove[0], stealMove[1], player.getSymbol());
        final String moveInfo = (stealMove[0] + 1) + " " + (stealMove[1] + 1);
        System.out.println(ConsoleColors.RED + "⚡ Хардкор-бот ЗАМЕНИЛ вашу ячейку " + moveInfo + "!" + ConsoleColors.RESET);
        return true;
    }

    protected int[] findPlayerCellToSteal(final Board board, final char opponentSymbol) {
        final List<int[]> playerCells = new ArrayList<>();

        for (int row = 0; row < GameConstants.BOARD_SIZE; row++) {
            for (int col = 0; col < GameConstants.BOARD_SIZE; col++) {
                if (board.getCell(row, col) == opponentSymbol) {
                    playerCells.add(new int[]{row, col});
                }
            }
        }

        return playerCells.isEmpty() ? null : playerCells.get(random.nextInt(playerCells.size()));
    }

    protected int[] getRandomMove(final Board board) {
        final List<int[]> freeCells = new ArrayList<>();
        for (int row = 0; row < GameConstants.BOARD_SIZE; row++) {
            for (int col = 0; col < GameConstants.BOARD_SIZE; col++) {
                if (board.isCellEmpty(row, col)) {
                    freeCells.add(new int[]{row, col});
                }
            }
        }
        return freeCells.isEmpty() ? null : freeCells.get(random.nextInt(freeCells.size()));
    }

    private char getOpponentSymbol(final char symbol) {
        return (symbol == GameConstants.PLAYER_X) ? GameConstants.PLAYER_O : GameConstants.PLAYER_X;
    }

    private boolean shouldStealCell() {
        return random.nextInt(GameConstants.PERCENTAGE_BASE) < GameConstants.STEAL_PROBABILITY_PERCENT;
    }
}