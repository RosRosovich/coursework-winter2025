package com.tictactoe.core;

import com.tictactoe.players.Player;
import com.tictactoe.storage.ColorManager;
import com.tictactoe.storage.ConsoleColors;

import java.util.Objects;
import java.util.Random;

public final class FirstMoveStrategies {

    public record SymbolBasedStrategy(char preferredSymbol) implements FirstMoveStrategy {
        public SymbolBasedStrategy {
            if (preferredSymbol != GameConstants.PLAYER_X && preferredSymbol != GameConstants.PLAYER_O) {
                throw new IllegalArgumentException("Preferred symbol must be 'X' or 'O'");
            }
        }

        @Override
        public Player determineFirstPlayer(final Player player1, final Player player2) {
            Objects.requireNonNull(player1, "Player1 cannot be null");
            Objects.requireNonNull(player2, "Player2 cannot be null");

            return (player1.getSymbol() == preferredSymbol) ? player1 : player2;
        }
    }

    public static class RandomStrategy implements FirstMoveStrategy {
        private final Random random;

        public RandomStrategy() {
            this.random = new Random();
        }

        @Override
        public Player determineFirstPlayer(final Player player1, final Player player2) {
            Objects.requireNonNull(player1, "Player1 cannot be null");
            Objects.requireNonNull(player2, "Player2 cannot be null");

            System.out.println("\n🎲 Бросаем кубики, чтобы определить, кто ходит первым...");

            final DiceResult diceResult = rollDiceUntilDifferent(player1, player2);
            final Player firstPlayer = (diceResult.dice1() > diceResult.dice2()) ? player1 : player2;

            announceFirstPlayer(firstPlayer);
            return firstPlayer;
        }

        private DiceResult rollDiceUntilDifferent(final Player player1, final Player player2) {
            Objects.requireNonNull(player1, "Player1 cannot be null");
            Objects.requireNonNull(player2, "Player2 cannot be null");

            int dice1;
            int dice2;

            do {
                dice1 = random.nextInt(GameConstants.DICE_SIDES) + 1;
                dice2 = random.nextInt(GameConstants.DICE_SIDES) + 1;
                System.out.println(getPlayerDisplayName(player1) + " выпал: " + dice1);
                System.out.println(getPlayerDisplayName(player2) + " выпал: " + dice2);
                if (dice1 == dice2) {
                    System.out.println("Ничья при броске — бросаем снова...");
                }
            } while (dice1 == dice2);

            return new DiceResult(dice1, dice2);
        }

        private void announceFirstPlayer(final Player firstPlayer) {
            Objects.requireNonNull(firstPlayer, "First player cannot be null");
            System.out.println("Первым ходит: " + getPlayerDisplayName(firstPlayer) + "\n");
        }

        private String getPlayerDisplayName(final Player player) {
            Objects.requireNonNull(player, "Player cannot be null");
            return ColorManager.getColorCode(ColorManager.getPlayerColor(player.getName()))
                    + player.getName() + " (" + player.getSymbol() + ")"
                    + ConsoleColors.RESET;
        }

        private record DiceResult(int dice1, int dice2) {}
    }

    public static FirstMoveStrategy createFromSettings() {
        final String firstMoveSetting = com.tictactoe.storage.SettingsManager.getFirstMove();
        return switch (firstMoveSetting) {
            case "X" -> new SymbolBasedStrategy(GameConstants.PLAYER_X);
            case "O" -> new SymbolBasedStrategy(GameConstants.PLAYER_O);
            default -> new RandomStrategy();
        };
    }
}