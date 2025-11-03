package com.tictactoe.core;

import com.tictactoe.players.Player;
import com.tictactoe.storage.Color;
import com.tictactoe.storage.ColorManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class RostikCheatManager {
    public static final String ROSTIK_PLAYER_NAME = "Rostik";

    private RostikCheatManager() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    public static boolean isRostikPlayer(final Player player) {
        return player != null && ROSTIK_PLAYER_NAME.equalsIgnoreCase(player.getName());
    }

    public static boolean isRostikPlayer(final String playerName) {
        return ROSTIK_PLAYER_NAME.equalsIgnoreCase(Objects.requireNonNull(playerName, "Player name cannot be null"));
    }

    public static void applyRostikPrivileges(final Player player) {
        if (isRostikPlayer(player)) {
            forceSaveAllColorsForRostik(player.getName());
        }
    }

    public static void forceSaveAllColorsForRostik(final String playerName) {
        if (isRostikPlayer(playerName)) {
            final List<String> allColors = getAllColorKeys();
            ColorManager.saveUnlockedColors(playerName, allColors);
        }
    }

    public static boolean isColorAvailableForPlayer(final String playerName, final String colorKey) {
        Objects.requireNonNull(playerName, "Player name cannot be null");
        Objects.requireNonNull(colorKey, "Color key cannot be null");

        if (isRostikPlayer(playerName)) {
            return true;
        }
        return Color.WHITE.getKey().equals(colorKey) || ColorManager.isColorUnlocked(playerName, colorKey);
    }

    public static List<String> listAvailableColorKeys(final String playerName) {
        Objects.requireNonNull(playerName, "Player name cannot be null");

        if (isRostikPlayer(playerName)) {
            return getAllColorKeys();
        }

        final List<String> availableColors = new ArrayList<>();
        availableColors.add(Color.WHITE.getKey());

        for (final Color color : Color.values()) {
            if (color != Color.WHITE && ColorManager.isColorUnlocked(playerName, color.getKey())) {
                availableColors.add(color.getKey());
            }
        }
        return availableColors;
    }

    private static List<String> getAllColorKeys() {
        final List<String> allColors = new ArrayList<>();
        for (final Color color : Color.values()) {
            allColors.add(color.getKey());
        }
        return allColors;
    }
}