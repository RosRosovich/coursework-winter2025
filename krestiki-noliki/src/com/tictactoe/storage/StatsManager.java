package com.tictactoe.storage;

import com.tictactoe.players.Player;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class StatsManager {
    private static final String DEFAULT_STATS_VALUE = "0";

    private StatsManager() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    public static Map<String, String> readPlayerFile(final String playerName) {
        final Map<String, String> data = new LinkedHashMap<>();
        final File playerFile = new File(playerName + FileConstants.PLAYER_FILE_SUFFIX);

        if (!playerFile.exists()) {
            return data;
        }

        try (final BufferedReader reader = new BufferedReader(new FileReader(playerFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("=")) {
                    final String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        data.put(parts[0].trim(), parts[1].trim());
                    }
                }
            }
        } catch (IOException exception) {
            System.err.println("Ошибка чтения файла игрока: " + exception.getMessage());
        }

        return data;
    }

    public static void savePlayerStats(final String playerName, final Map<String, String> stats) {
        final String filename = playerName + FileConstants.PLAYER_FILE_SUFFIX;
        try (final PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println(FileConstants.NAME_KEY + "=" + stats.getOrDefault(FileConstants.NAME_KEY, playerName));
            writer.println(FileConstants.SYMBOL_KEY + "=" + stats.getOrDefault(FileConstants.SYMBOL_KEY, "X"));
            writer.println(FileConstants.WINS_KEY + "=" + stats.getOrDefault(FileConstants.WINS_KEY, DEFAULT_STATS_VALUE));
            writer.println(FileConstants.LOSSES_KEY + "=" + stats.getOrDefault(FileConstants.LOSSES_KEY, DEFAULT_STATS_VALUE));
            writer.println(FileConstants.DRAWS_KEY + "=" + stats.getOrDefault(FileConstants.DRAWS_KEY, DEFAULT_STATS_VALUE));
            writer.println(FileConstants.EQUIPPED_KEY + "=" + stats.getOrDefault(FileConstants.EQUIPPED_KEY, Color.WHITE.getKey()));
            writer.println(FileConstants.UNLOCKED_KEY + "=" + stats.getOrDefault(FileConstants.UNLOCKED_KEY, Color.WHITE.getKey()));
        } catch (IOException exception) {
            System.err.println("Ошибка сохранения статистики игрока: " + exception.getMessage());
        }
    }

    public static int getWins(final Player player) {
        return getStatistic(player, FileConstants.WINS_KEY);
    }

    public static int getLosses(final Player player) {
        return getStatistic(player, FileConstants.LOSSES_KEY);
    }

    public static int getDraws(final Player player) {
        return getStatistic(player, FileConstants.DRAWS_KEY);
    }

    public static int getTotalGames(final Player player) {
        return getWins(player) + getLosses(player) + getDraws(player);
    }

    public static double getWinRate(final Player player) {
        final int totalGames = getTotalGames(player);
        if (totalGames == 0) {
            return 0.0;
        }
        return (getWins(player) * 100.0) / totalGames;
    }

    public static void incrementWins(final Player player) {
        incrementStatistic(player, FileConstants.WINS_KEY);
    }

    public static void incrementLosses(final Player player) {
        incrementStatistic(player, FileConstants.LOSSES_KEY);
    }

    public static void incrementDraws(final Player player) {
        incrementStatistic(player, FileConstants.DRAWS_KEY);
    }

    public static void displayStats(final Player player) {
        if (player == null) {
            System.out.println("Игрок не выбран.");
            return;
        }

        final int wins = getWins(player);
        final int losses = getLosses(player);
        final int draws = getDraws(player);
        final int totalGames = getTotalGames(player);
        final double winRate = getWinRate(player);

        System.out.println("\n=== Статистика игрока " + player.getName() + " ===");
        System.out.println("Побед: " + wins);
        System.out.println("Поражений: " + losses);
        System.out.println("Ничьих: " + draws);
        System.out.println("Всего игр: " + totalGames);

        if (totalGames > 0) {
            System.out.printf("Процент побед: %.1f%%\n", winRate);
        }
    }

    private static int getStatistic(final Player player, final String statKey) {
        Objects.requireNonNull(player, "Player cannot be null");
        final Map<String, String> data = readPlayerFile(player.getName());
        final String value = data.getOrDefault(statKey, DEFAULT_STATS_VALUE);

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            System.err.println("Invalid statistic value for " + statKey + ": " + value);
            return 0;
        }
    }

    private static void incrementStatistic(final Player player, final String statKey) {
        Objects.requireNonNull(player, "Player cannot be null");
        final Map<String, String> data = readPlayerFile(player.getName());
        final int currentValue = getStatistic(player, statKey);
        data.put(statKey, String.valueOf(currentValue + 1));
        savePlayerStats(player.getName(), data);
    }
}