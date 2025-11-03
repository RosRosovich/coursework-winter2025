package com.tictactoe.players;

import com.tictactoe.core.Board;
import com.tictactoe.core.GameConstants;
import com.tictactoe.core.RostikCheatManager;
import com.tictactoe.storage.ConsoleColors;
import com.tictactoe.storage.SettingsManager;
import com.tictactoe.storage.StatsManager;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class BotPlayer extends Player {
    private final BotMoveStrategy moveStrategy;
    private final boolean hardMode;

    public BotPlayer(final String name, final char symbol, final boolean hardMode) {
        super(name, symbol);
        this.hardMode = hardMode;
        this.moveStrategy = new BotMoveStrategy(hardMode);
    }

    public static boolean isHardcoreAvailable(final Player player) {
        if (player == null) {
            return false;
        }
        if (RostikCheatManager.isRostikPlayer(player)) {
            return true;
        }

        final int wins = StatsManager.getWins(player);
        return wins >= GameConstants.WINS_REQUIRED_FOR_HARDCORE;
    }

    public static void showHardcoreWarning() {
        System.out.println(ConsoleColors.RED + "\n⚠️  ВНИМАНИЕ: ХАРДКОР РЕЖИМ!" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW + "Бот получил новые способности:" + ConsoleColors.RESET);
        System.out.println("- Может заменять ваши уже поставленные фигуры");
        System.out.println("- Для победы потребуется УДАЧА и СТРАТЕГИЯ");
        System.out.println(ConsoleColors.RED + "- В хардкоре первый ход всегда случайный!" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.RED + "Вы готовы принять вызов?!" + ConsoleColors.RESET);
    }

    @Override
    public boolean makeMove(final Board board, final Scanner scanner) {
        if (board == null || board.isFull()) {
            return false;
        }

        if (SettingsManager.isBotThinkingEnabled()) {
            simulateThinking();
        }

        return moveStrategy.makeMove(board, this);
    }

    private void simulateThinking() {
        try {
            if (hardMode) {
                System.out.println(ConsoleColors.RED + "🤖 Хардкор-бот обдумывает ход..." + ConsoleColors.RESET);
            } else {
                System.out.println("🤖 Бот обдумывает ход...");
            }
            TimeUnit.MILLISECONDS.sleep(GameConstants.THINKING_DELAY_MILLIS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            System.out.println("Thinking interrupted");
        }
    }

    public boolean isHardMode() {
        return hardMode;
    }
}