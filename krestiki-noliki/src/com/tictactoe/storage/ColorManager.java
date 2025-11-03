package com.tictactoe.storage;

import com.tictactoe.core.RostikCheatManager;
import com.tictactoe.players.Player;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public final class ColorManager {
    private static final String UNLOCKED_KEY = "unlocked";
    private static final String EQUIPPED_KEY = "equipped";

    private static final Map<Integer, Color> WIN_COLORS = Map.of(
            1, Color.GREEN,
            2, Color.BLUE,
            5, Color.TEAL,
            10, Color.DARK_GREEN,
            25, Color.VIOLET
    );

    private static final Map<Integer, Color> LOSS_COLORS = Map.of(
            1, Color.RED,
            2, Color.PURPLE,
            5, Color.PINK,
            10, Color.YELLOW,
            25, Color.CRIMSON
    );

    private static final Map<String, Color> PROMO_COLORS = Map.of(
            "VSTU", Color.CYAN
    );

    private ColorManager() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    public static void checkNewUnlockedColors(Player player) {
        Objects.requireNonNull(player, "Player cannot be null");
        String playerName = player.getName();

        if (!PlayerManager.playerExists(playerName)) {
            return;
        }

        if (RostikCheatManager.isRostikPlayer(player)) {
            RostikCheatManager.applyRostikPrivileges(player);
            return;
        }

        int wins = StatsManager.getWins(player);
        int losses = StatsManager.getLosses(player);

        unlockColorsByThreshold(playerName, wins, WIN_COLORS, "побед");
        unlockColorsByThreshold(playerName, losses, LOSS_COLORS, "поражений");
    }

    private static void unlockColorsByThreshold(String playerName, int value,
                                                Map<Integer, Color> colorMap, String achievement) {
        for (Map.Entry<Integer, Color> entry : colorMap.entrySet()) {
            if (value >= entry.getKey() && !isColorUnlocked(playerName, entry.getValue().getKey())) {
                unlockColor(playerName, entry.getValue().getKey());
                System.out.println("🎉 Новый цвет за " + entry.getKey() + " " + achievement + ": " + entry.getValue().getDisplayName());
            }
        }
    }

    public static void showMyColors(Player player) {
        Objects.requireNonNull(player, "Player cannot be null");
        List<String> availableColors = getAvailableColorKeys(player.getName());
        String equippedColor = getEquippedColor(player.getName());

        System.out.println("\n=== Мои цвета (" + player.getName() + ") ===");
        System.out.println("Всего цветов: " + availableColors.size() + " из " + Color.values().length);

        if (availableColors.isEmpty()) {
            System.out.println("У игрока пока нет доступных цветов.");
        } else {
            for (String key : availableColors) {
                Color color = Color.fromKey(key);
                String equippedMarker = key.equals(equippedColor) ? " (надет)" : "";
                System.out.println(" ✅ " + key + " - " + color.getDisplayName() + equippedMarker);
            }
        }
    }

    public static void showAllColors(Player player) {
        Objects.requireNonNull(player, "Player cannot be null");

        if (RostikCheatManager.isRostikPlayer(player)) {
            RostikCheatManager.applyRostikPrivileges(player);
        }

        int wins = StatsManager.getWins(player);
        int losses = StatsManager.getLosses(player);
        int totalGames = StatsManager.getTotalGames(player);
        double winRate = StatsManager.getWinRate(player);
        String equippedColor = getEquippedColor(player.getName());

        System.out.println("\n=== Все цвета ===");
        System.out.println("Победы: " + wins + " | Поражения: " + losses + " | Всего игр: " + totalGames);
        System.out.printf("Процент побед: %.1f%%\n", winRate);

        displayColorCategory("Базовые цвета:", List.of(Color.WHITE), wins, losses, equippedColor, player);
        displayColorCategory("За победы:", new ArrayList<>(WIN_COLORS.values()), wins, losses, equippedColor, player);
        displayColorCategory("За поражения:", new ArrayList<>(LOSS_COLORS.values()), wins, losses, equippedColor, player);
        displayPromoColors(equippedColor, player);

        if (RostikCheatManager.isRostikPlayer(player)) {
            System.out.println("\n🎉 У Rostik открыты все цвета! (" + getAvailableColorKeys(player.getName()).size() + " из " + Color.values().length + ")");
        }
    }

    private static void displayColorCategory(String title, List<Color> colors, int wins, int losses,
                                             String equippedColor, Player player) {
        System.out.println("\n" + title);
        for (Color color : colors) {
            boolean isUnlocked = isColorUnlocked(player.getName(), color.getKey());
            String mark = isUnlocked ? "✅" : "❌";
            String equippedMarker = color.getKey().equals(equippedColor) ? " (надет)" : "";
            String requirement = getRequirementText(color, wins, losses);

            System.out.println(" " + mark + " " + color.getKey() + " - " + color.getDisplayName() + requirement + equippedMarker);
        }
    }

    private static String getRequirementText(Color color, int wins, int losses) {
        if (WIN_COLORS.containsValue(color)) {
            for (Map.Entry<Integer, Color> entry : WIN_COLORS.entrySet()) {
                if (entry.getValue() == color) {
                    return wins >= entry.getKey() ? "" : " (нужно " + entry.getKey() + " побед)";
                }
            }
        } else if (LOSS_COLORS.containsValue(color)) {
            for (Map.Entry<Integer, Color> entry : LOSS_COLORS.entrySet()) {
                if (entry.getValue() == color) {
                    return losses >= entry.getKey() ? "" : " (нужно " + entry.getKey() + " поражений)";
                }
            }
        }
        return "";
    }

    private static void displayPromoColors(String equippedColor, Player player) {
        System.out.println("\nПромокоды:");
        for (Map.Entry<String, Color> entry : PROMO_COLORS.entrySet()) {
            String code = entry.getKey();
            Color color = entry.getValue();
            boolean isUnlocked = isColorUnlocked(player.getName(), color.getKey());
            String mark = isUnlocked ? "✅" : "❌";
            String equippedMarker = color.getKey().equals(equippedColor) ? " (надет)" : "";
            String status = isUnlocked ? " (активирован)" : " (не активирован)";

            System.out.println(" " + mark + " " + color.getKey() + " — " + color.getDisplayName()
                    + status + " (промокод: " + code + ")" + equippedMarker);
        }
    }

    public static void applyPromoCode(Player player, String code) {
        Objects.requireNonNull(player, "Player cannot be null");
        Objects.requireNonNull(code, "Promo code cannot be null");

        String playerName = player.getName();
        if (!PlayerManager.playerExists(playerName)) {
            System.out.println("Сначала сохраните/загрузите игрока.");
            return;
        }

        if (PROMO_COLORS.containsKey(code)) {
            Color color = PROMO_COLORS.get(code);

            if (isColorUnlocked(playerName, color.getKey())) {
                System.out.println("❌ Вы уже активировали промокод " + code + "!");
                System.out.println("Цвет " + color.getDisplayName() + " уже доступен в вашей коллекции.");
                return;
            }

            unlockColor(playerName, color.getKey());
            System.out.println("✅ Промокод принят, теперь доступен цвет: " + color.getDisplayName());
        } else {
            System.out.println("❌ Неверный промокод.");
        }
    }

    public static void equipColor(String playerName, String colorKey) {
        Objects.requireNonNull(playerName, "Player name cannot be null");
        Objects.requireNonNull(colorKey, "Color key cannot be null");

        colorKey = colorKey.toLowerCase();

        // Проверяем валидность ключа цвета
        if (!isValidColorKey(colorKey)) {
            System.out.println("❌ Цвет '" + colorKey + "' не существует.");
            return;
        }

        Color color = Color.fromKey(colorKey);

        if (!isColorAvailableForPlayer(playerName, colorKey)) {
            System.out.println("❌ Цвет " + color.getDisplayName() + " ещё не доступен.");
            return;
        }

        saveEquippedColor(playerName, colorKey);
        System.out.println("🎨 Игрок " + playerName + " надел цвет: " + color.getDisplayName());
    }

    public static void equipRandomColorForPlayer(String playerName) {
        Objects.requireNonNull(playerName, "Player name cannot be null");

        List<String> availableColors = getAvailableColorKeys(playerName);
        if (availableColors.isEmpty()) {
            System.out.println("Нет доступных цветов для игрока " + playerName);
            return;
        }

        String pick = availableColors.get(new Random().nextInt(availableColors.size()));
        equipColor(playerName, pick);
    }

    public static String getPlayerColor(String playerName) {
        return getEquippedColor(playerName);
    }

    public static String getColorCode(String colorKey) {
        return Color.fromKey(colorKey).getColorCode();
    }

    public static String getDisplayName(String key) {
        return Color.fromKey(key).getDisplayName();
    }

    public static void saveUnlockedColors(String playerName, List<String> colors) {
        Objects.requireNonNull(playerName, "Player name cannot be null");
        Objects.requireNonNull(colors, "Colors list cannot be null");

        Map<String, String> data = StatsManager.readPlayerFile(playerName);
        data.put(UNLOCKED_KEY, String.join(",", colors));
        savePlayerData(playerName, data);
    }

    public static boolean isColorUnlocked(String playerName, String colorKey) {
        return getUnlockedColors(playerName).contains(colorKey);
    }

    public static List<String> getAvailableColorKeys(String playerName) {
        return RostikCheatManager.listAvailableColorKeys(playerName);
    }

    private static boolean isColorAvailableForPlayer(String playerName, String colorKey) {
        return RostikCheatManager.isColorAvailableForPlayer(playerName, colorKey);
    }

    private static boolean isValidColorKey(String colorKey) {
        try {
            Color.fromKey(colorKey);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static void unlockColor(String playerName, String colorKey) {
        List<String> unlockedColors = getUnlockedColors(playerName);
        if (!unlockedColors.contains(colorKey)) {
            List<String> newUnlockedColors = new ArrayList<>(unlockedColors);
            newUnlockedColors.add(colorKey);
            saveUnlockedColors(playerName, newUnlockedColors);
        }
    }

    private static void saveEquippedColor(String playerName, String colorKey) {
        Map<String, String> data = StatsManager.readPlayerFile(playerName);
        data.put(EQUIPPED_KEY, colorKey);
        savePlayerData(playerName, data);
    }

    private static String getEquippedColor(String playerName) {
        Map<String, String> data = StatsManager.readPlayerFile(playerName);
        return data.getOrDefault(EQUIPPED_KEY, Color.WHITE.getKey());
    }

    private static List<String> getUnlockedColors(String playerName) {
        if (RostikCheatManager.isRostikPlayer(playerName)) {
            return RostikCheatManager.listAvailableColorKeys(playerName);
        }

        Map<String, String> data = StatsManager.readPlayerFile(playerName);
        String unlockedValue = data.getOrDefault(UNLOCKED_KEY, Color.WHITE.getKey());

        if (unlockedValue.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<String> colors = new ArrayList<>();
        for (String color : unlockedValue.split(",")) {
            String trimmedColor = color.trim();
            if (!trimmedColor.isEmpty()) {
                colors.add(trimmedColor);
            }
        }
        return colors;
    }

    private static void savePlayerData(String playerName, Map<String, String> data) {
        String filename = playerName + FileConstants.PLAYER_FILE_SUFFIX;

        Map<String, String> orderedData = new LinkedHashMap<>();
        orderedData.put(FileConstants.NAME_KEY, data.getOrDefault(FileConstants.NAME_KEY, playerName));
        orderedData.put(FileConstants.SYMBOL_KEY, data.getOrDefault(FileConstants.SYMBOL_KEY, "X"));
        orderedData.put(FileConstants.WINS_KEY, data.getOrDefault(FileConstants.WINS_KEY, FileConstants.DEFAULT_VALUE));
        orderedData.put(FileConstants.LOSSES_KEY, data.getOrDefault(FileConstants.LOSSES_KEY, FileConstants.DEFAULT_VALUE));
        orderedData.put(FileConstants.DRAWS_KEY, data.getOrDefault(FileConstants.DRAWS_KEY, FileConstants.DEFAULT_VALUE));
        orderedData.put(FileConstants.EQUIPPED_KEY, data.getOrDefault(FileConstants.EQUIPPED_KEY, Color.WHITE.getKey()));
        orderedData.put(FileConstants.UNLOCKED_KEY, data.getOrDefault(FileConstants.UNLOCKED_KEY, Color.WHITE.getKey()));

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Map.Entry<String, String> entry : orderedData.entrySet()) {
                writer.println(entry.getKey() + "=" + entry.getValue());
            }
        } catch (IOException exception) {
            System.err.println("Ошибка сохранения данных игрока: " + exception.getMessage());
        }
    }
}