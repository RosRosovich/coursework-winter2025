package com.tictactoe.core;

import com.tictactoe.players.Player;
import com.tictactoe.storage.ColorManager;
import com.tictactoe.storage.ConsoleColors;

import java.util.Objects;
import java.util.Scanner;

public final class Game {
    private final Board board;
    private final Player player1;
    private final Player player2;
    private final FirstMoveStrategy firstMoveStrategy;

    public Game(Player player1, Player player2) {
        this.board = new Board();
        this.player1 = Objects.requireNonNull(player1, "Player 1 cannot be null");
        this.player2 = Objects.requireNonNull(player2, "Player 2 cannot be null");
        this.firstMoveStrategy = FirstMoveStrategies.createFromSettings();

        validatePlayers();
    }

    private void validatePlayers() {
        if (player1.getSymbol() == player2.getSymbol()) {
            throw new IllegalArgumentException("Both players cannot have the same symbol: " + player1.getSymbol());
        }
    }

    public GameResult start() {
        Scanner scanner = new Scanner(System.in);
        Player currentPlayer = determineFirstPlayer();

        while (true) {
            board.printBoard(player1, player2);
            System.out.println();
            System.out.println(colorizeName(currentPlayer) + " делает ход:");

            boolean validMove = currentPlayer.makeMove(board, scanner);
            if (!validMove) {
                continue;
            }

            GameResult result = checkGameResult();
            if (result.hasWinner() || result.isDraw()) {
                board.printBoard(player1, player2);
                announceResult(result);
                return result;
            }

            currentPlayer = switchPlayer(currentPlayer);
        }
    }

    private Player determineFirstPlayer() {
        return firstMoveStrategy.determineFirstPlayer(player1, player2);
    }

    private GameResult checkGameResult() {
        char winnerSymbol = board.checkWin();
        if (winnerSymbol != Board.EMPTY_CELL) {
            String winnerName = getPlayerNameBySymbol(winnerSymbol);
            return GameResult.win(winnerName);
        }

        if (board.isFull()) {
            return GameResult.draw();
        }

        return GameResult.none();
    }

    private String getPlayerNameBySymbol(char symbol) {
        if (player1.getSymbol() == symbol) {
            return player1.getName();
        } else if (player2.getSymbol() == symbol) {
            return player2.getName();
        }
        throw new IllegalStateException("Unknown player symbol: " + symbol);
    }

    private void announceResult(GameResult result) {
        Objects.requireNonNull(result, "Game result cannot be null");

        if (result.hasWinner()) {
            Player winner = getPlayerByName(result.winner());
            Player loser = getOpponent(winner);

            System.out.println(ConsoleColors.LIME + "🎉 Победил " + colorizeName(winner) + "!" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.RED + "💀 Проиграл " + colorizeName(loser) + ConsoleColors.RESET);
        } else if (result.isDraw()) {
            System.out.println(ConsoleColors.YELLOW + "🤝 Ничья!" + ConsoleColors.RESET);
        }
    }

    private Player getPlayerByName(String name) {
        Objects.requireNonNull(name, "Player name cannot be null");

        if (player1.getName().equals(name)) return player1;
        if (player2.getName().equals(name)) return player2;
        throw new IllegalArgumentException("Player not found: " + name);
    }

    private Player getOpponent(Player player) {
        Objects.requireNonNull(player, "Player cannot be null");
        return (player == player1) ? player2 : player1;
    }

    private Player switchPlayer(Player currentPlayer) {
        Objects.requireNonNull(currentPlayer, "Current player cannot be null");
        return (currentPlayer == player1) ? player2 : player1;
    }

    private String colorizeName(Player player) {
        Objects.requireNonNull(player, "Player cannot be null");

        String colorKey = ColorManager.getPlayerColor(player.getName());
        String colorCode = ColorManager.getColorCode(colorKey);
        return colorCode + player.getName() + " (" + player.getSymbol() + ")" + ConsoleColors.RESET;
    }
}