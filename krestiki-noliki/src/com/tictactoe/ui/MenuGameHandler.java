package com.tictactoe.ui;

import com.tictactoe.core.Game;
import com.tictactoe.core.GameConstants;
import com.tictactoe.core.GameResult;
import com.tictactoe.core.GameType;
import com.tictactoe.players.BotPlayer;
import com.tictactoe.players.Player;
import com.tictactoe.storage.ColorManager;
import com.tictactoe.storage.PlayerManager;
import com.tictactoe.storage.StatsManager;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public final class MenuGameHandler {
    private static final String BOT_NAME = "Bot";
    private static final String NORMAL_DIFFICULTY = "1";
    private static final String HARDCORE_DIFFICULTY = "2";

    private MenuGameHandler() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    public static void startBotGame(final Scanner scanner, final Player currentPlayer) {
        Objects.requireNonNull(scanner, "Scanner cannot be null");

        if (isGameStartConfirmed(scanner, currentPlayer)) {
            return;
        }

        final boolean isHardcore = selectBotDifficulty(scanner);
        if (isHardcore && !BotPlayer.isHardcoreAvailable(currentPlayer)) {
            final int wins = StatsManager.getWins(currentPlayer);
            System.out.println("❌ Хардкор режим недоступен!");
            System.out.println("Нужно " + GameConstants.WINS_REQUIRED_FOR_HARDCORE + " побед. У вас: " + wins + " побед.");
            return;
        }

        if (isHardcore && !isHardcoreModeConfirmed(scanner)) {
            return;
        }

        final char botSymbol = getOppositeSymbol(currentPlayer.getSymbol());
        final Player bot = new BotPlayer(BOT_NAME, botSymbol, isHardcore);

        final Game game = new Game(currentPlayer, bot);
        final GameResult result = game.start();
        updateStatsAfterGame(result, GameType.VS_BOT, currentPlayer, bot);
    }

    public static void startSelfGame(final Scanner scanner, final Player currentPlayer) {
        Objects.requireNonNull(scanner, "Scanner cannot be null");

        if (isGameStartConfirmed(scanner, currentPlayer)) {
            return;
        }

        System.out.println("⚠️  Игра с самим собой не засчитывается в статистику!");

        final Player secondPlayer = selectSecondPlayer(scanner);
        if (secondPlayer == null) {
            return;
        }

        if (!validatePlayerSymbols(currentPlayer, secondPlayer)) {
            return;
        }

        final Game game = new Game(currentPlayer, secondPlayer);
        final GameResult result = game.start();
        updateStatsAfterGame(result, GameType.SELF_GAME, currentPlayer, secondPlayer);
    }

    private static boolean isGameStartConfirmed(final Scanner scanner, final Player currentPlayer) {
        if (currentPlayer == null) {
            System.out.println("Сначала создайте или загрузите игрока.");
            return true;
        }
        return !MenuPlayerHandler.confirmAction(scanner, "Точно хотите начать игру?");
    }

    private static boolean isHardcoreModeConfirmed(final Scanner scanner) {
        BotPlayer.showHardcoreWarning();
        return MenuPlayerHandler.confirmAction(scanner, "Подтвердите начало игры в хардкор режиме");
    }

    private static Player selectSecondPlayer(final Scanner scanner) {
        final List<String> allPlayers = PlayerManager.listPlayers();
        if (allPlayers.size() < 2) {
            System.out.println("Для игры с самим собой требуется минимум 2 сохранённых игрока.");
            return null;
        }

        MenuPlayerHandler.displayPlayerList(allPlayers, "Выберите второго игрока:");
        System.out.print("Введите номер или имя второго игрока: ");

        final String input = scanner.nextLine().trim();
        final String secondPlayerName = MenuPlayerHandler.findPlayerByNameOrIndex(allPlayers, input, scanner);

        if (secondPlayerName == null) {
            System.out.println("Игрок не найден.");
            return null;
        }

        return PlayerManager.loadPlayer(secondPlayerName, GameConstants.PLAYER_X);
    }

    private static boolean validatePlayerSymbols(final Player player1, final Player player2) {
        if (player1.getSymbol() == player2.getSymbol()) {
            System.out.println("❌ Оба игрока не могут играть одним и тем же символом! "
                    + "Один должен быть X, другой — O.");
            return false;
        }
        return true;
    }

    private static boolean selectBotDifficulty(final Scanner scanner) {
        Objects.requireNonNull(scanner, "Scanner cannot be null");

        while (true) {
            System.out.println("Выберите режим бота: 1 - Обычный, 2 - Хардкор");
            final String difficulty = scanner.nextLine().trim();

            if (difficulty.equals(NORMAL_DIFFICULTY)) {
                return false;
            } else if (difficulty.equals(HARDCORE_DIFFICULTY)) {
                return true;
            } else {
                System.out.println("❌ Ошибка: введите 1 или 2");
            }
        }
    }

    private static char getOppositeSymbol(final char symbol) {
        return (symbol == GameConstants.PLAYER_X) ? GameConstants.PLAYER_O : GameConstants.PLAYER_X;
    }

    private static void updateStatsAfterGame(final GameResult result, final GameType gameType,
                                             final Player currentPlayer, final Player opponent) {
        if (result == null) {
            return;
        }

        final GameResultHandler handler = createGameResultHandler(gameType);
        handler.handle(result, currentPlayer, opponent);
    }

    private static GameResultHandler createGameResultHandler(final GameType gameType) {
        return switch (gameType) {
            case SELF_GAME -> new SelfGameResultHandler();
            case VS_BOT -> new BotGameResultHandler();
        };
    }

    private interface GameResultHandler {
        void handle(GameResult result, Player currentPlayer, Player opponent);
    }

    private static class SelfGameResultHandler implements GameResultHandler {
        @Override
        public void handle(final GameResult result, final Player currentPlayer, final Player opponent) {
            announceResult(result, currentPlayer, opponent);
            System.out.println("ℹ️ Игра с самим собой не засчитывается в статистику");
        }
    }

    private static class BotGameResultHandler implements GameResultHandler {
        @Override
        public void handle(final GameResult result, final Player currentPlayer, final Player opponent) {
            if (result.hasWinner()) {
                handleWin(result, currentPlayer);
            } else if (result.isDraw()) {
                handleDraw(currentPlayer);
            }
            updateColorsAfterGame(currentPlayer);
        }

        private void handleWin(final GameResult result, final Player currentPlayer) {
            if (result.winner().equals(currentPlayer.getName())) {
                StatsManager.incrementWins(currentPlayer);
                System.out.println("🎉 " + currentPlayer.getName() + " побеждает!");
            } else {
                StatsManager.incrementLosses(currentPlayer);
                System.out.println("❌ Бот побеждает!");
            }
        }

        private void handleDraw(final Player currentPlayer) {
            StatsManager.incrementDraws(currentPlayer);
            System.out.println("🤝 Ничья!");
        }
    }

    private static void announceResult(final GameResult result, final Player currentPlayer, final Player opponent) {
        if (result.hasWinner()) {
            final String winnerName = result.winner();
            if (winnerName.equals(currentPlayer.getName())) {
                System.out.println("🎉 " + currentPlayer.getName() + " побеждает!");
            } else {
                System.out.println("🎉 " + opponent.getName() + " побеждает!");
            }
        } else if (result.isDraw()) {
            System.out.println("🤝 Ничья!");
        }
    }

    private static void updateColorsAfterGame(final Player player) {
        ColorManager.checkNewUnlockedColors(player);
    }
}