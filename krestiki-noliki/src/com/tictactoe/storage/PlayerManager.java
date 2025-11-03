package com.tictactoe.storage;

import com.tictactoe.core.RostikCheatManager;
import com.tictactoe.players.HumanPlayer;
import com.tictactoe.players.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public final class PlayerManager {

    private PlayerManager() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    public static boolean playerExists(final String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        final File playerFile = new File(name.trim() + FileConstants.PLAYER_FILE_SUFFIX);
        return playerFile.exists();
    }

    public static void savePlayer(final Player player) {
        Objects.requireNonNull(player, "Player cannot be null");

        final String filename = player.getName() + FileConstants.PLAYER_FILE_SUFFIX;
        final Map<String, String> existingData = readPlayerFile(player.getName());
        final Map<String, String> data = createPlayerData(player, existingData);

        savePlayerData(filename, data);
    }

    public static Player loadPlayer(final String name, final char symbol) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }

        final File playerFile = new File(name.trim() + FileConstants.PLAYER_FILE_SUFFIX);
        if (!playerFile.exists()) {
            return null;
        }

        try (final BufferedReader reader = new BufferedReader(new FileReader(playerFile))) {
            final PlayerData playerData = parsePlayerData(reader, name, symbol);
            return createPlayerFromData(playerData);
        } catch (IOException exception) {
            System.err.println("Ошибка загрузки игрока: " + exception.getMessage());
            return null;
        }
    }

    public static List<String> listPlayers() {
        final File currentDirectory = new File(".");
        final File[] playerFiles = currentDirectory.listFiles(
                (_, fileName) -> fileName.endsWith(FileConstants.PLAYER_FILE_SUFFIX));

        if (playerFiles == null) {
            return new ArrayList<>();
        }

        return Arrays.stream(playerFiles)
                .sorted(Comparator.comparingLong(File::lastModified))
                .map(File::getName)
                .map(fileName -> fileName.substring(0, fileName.length() - FileConstants.PLAYER_FILE_SUFFIX.length()))
                .collect(Collectors.toList());
    }

    public static void displayPlayerInfo(final Player player) {
        if (player == null || !playerExists(player.getName())) {
            System.out.println("Игрок " + (player != null ? player.getName() : "null") + " не найден.");
            return;
        }

        StatsManager.displayStats(player);

        final int totalColors = ColorManager.getAvailableColorKeys(player.getName()).size();
        final int maxColors = Color.values().length;
        System.out.println("Доступно цветов: " + totalColors + " из " + maxColors + " (посмотреть в разделе Цвета → Мои цвета)");
    }

    private static Map<String, String> createPlayerData(final Player player, final Map<String, String> existingData) {
        final Map<String, String> data = new LinkedHashMap<>();
        data.put(FileConstants.NAME_KEY, player.getName());
        data.put(FileConstants.SYMBOL_KEY, String.valueOf(player.getSymbol()));
        data.put(FileConstants.WINS_KEY, getOrDefault(existingData, FileConstants.WINS_KEY, FileConstants.DEFAULT_VALUE));
        data.put(FileConstants.LOSSES_KEY, getOrDefault(existingData, FileConstants.LOSSES_KEY, FileConstants.DEFAULT_VALUE));
        data.put(FileConstants.DRAWS_KEY, getOrDefault(existingData, FileConstants.DRAWS_KEY, FileConstants.DEFAULT_VALUE));
        data.put(FileConstants.EQUIPPED_KEY, getOrDefault(existingData, FileConstants.EQUIPPED_KEY, Color.WHITE.getKey()));

        if (RostikCheatManager.isRostikPlayer(player.getName())) {
            final List<String> allColors = Arrays.stream(Color.values())
                    .map(Color::getKey)
                    .collect(Collectors.toList());
            data.put(FileConstants.UNLOCKED_KEY, String.join(",", allColors));
        } else {
            data.put(FileConstants.UNLOCKED_KEY, getOrDefault(existingData, FileConstants.UNLOCKED_KEY, Color.WHITE.getKey()));
        }

        return data;
    }

    private static PlayerData parsePlayerData(final BufferedReader reader, final String defaultName, final char defaultSymbol) throws IOException {
        String playerName = defaultName;
        char playerSymbol = defaultSymbol;
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.contains("=")) {
                final String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    if (FileConstants.NAME_KEY.equals(parts[0])) {
                        playerName = parts[1].trim();
                    } else if (FileConstants.SYMBOL_KEY.equals(parts[0])) {
                        final String symbolString = parts[1].trim();
                        if (!symbolString.isEmpty()) {
                            playerSymbol = symbolString.charAt(0);
                        }
                    }
                }
            }
        }

        return new PlayerData(playerName, playerSymbol);
    }

    private static Player createPlayerFromData(final PlayerData playerData) {
        final Player player = new HumanPlayer(playerData.name(), playerData.symbol(), new Scanner(System.in));

        if (RostikCheatManager.isRostikPlayer(playerData.name())) {
            RostikCheatManager.applyRostikPrivileges(player);
        }

        return player;
    }

    private static Map<String, String> readPlayerFile(final String playerName) {
        return StatsManager.readPlayerFile(playerName);
    }

    private static void savePlayerData(final String filename, final Map<String, String> data) {
        try (final PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (final Map.Entry<String, String> entry : data.entrySet()) {
                writer.println(entry.getKey() + "=" + entry.getValue());
            }
        } catch (IOException exception) {
            System.err.println("Ошибка сохранения данных игрока: " + exception.getMessage());
            throw new RuntimeException("Не удалось сохранить игрока: " + exception.getMessage(), exception);
        }
    }

    private static String getOrDefault(final Map<String, String> map, final String key, final String defaultValue) {
        return map.getOrDefault(key, defaultValue);
    }

    private record PlayerData(String name, char symbol) {}
}