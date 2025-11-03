package com.tictactoe.storage;

import java.io.*;
import java.util.Objects;
import java.util.Properties;

public final class SettingsManager {
    private static final String CONFIRMATIONS_ENABLED = "enabled";
    private static final String CONFIRMATIONS_DISABLED = "disabled";
    private static final String FIRST_MOVE_X = "X";
    private static final String FIRST_MOVE_O = "O";
    private static final String FIRST_MOVE_RANDOM = "random";
    private static final String BOT_THINKING_ENABLED = "enabled";
    private static final String BOT_THINKING_DISABLED = "disabled";

    private static final String CONFIRMATIONS_KEY = "confirmations";
    private static final String FIRST_MOVE_KEY = "firstMove";
    private static final String BOT_THINKING_KEY = "botThinking";

    private static final Properties settings = new Properties();

    static {
        loadSettings();
    }

    private SettingsManager() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    public static boolean isConfirmationsEnabled() {
        return CONFIRMATIONS_ENABLED.equals(settings.getProperty(CONFIRMATIONS_KEY));
    }

    public static void setConfirmationsEnabled(final boolean enabled) {
        settings.setProperty(CONFIRMATIONS_KEY, enabled ? CONFIRMATIONS_ENABLED : CONFIRMATIONS_DISABLED);
        saveSettings();
    }

    public static String getFirstMove() {
        return settings.getProperty(FIRST_MOVE_KEY, FIRST_MOVE_RANDOM);
    }

    public static void setFirstMove(final String firstMove) {
        Objects.requireNonNull(firstMove, "First move setting cannot be null");

        if (isValidFirstMoveSetting(firstMove)) {
            settings.setProperty(FIRST_MOVE_KEY, firstMove);
            saveSettings();
        } else {
            throw new IllegalArgumentException("Invalid first move setting: " + firstMove);
        }
    }

    public static boolean isBotThinkingEnabled() {
        return BOT_THINKING_ENABLED.equals(settings.getProperty(BOT_THINKING_KEY));
    }

    public static void setBotThinkingEnabled(final boolean enabled) {
        settings.setProperty(BOT_THINKING_KEY, enabled ? BOT_THINKING_ENABLED : BOT_THINKING_DISABLED);
        saveSettings();
    }

    private static void loadSettings() {
        final File file = new File(FileConstants.SETTINGS_FILE);
        if (file.exists()) {
            try (final InputStream input = new FileInputStream(FileConstants.SETTINGS_FILE)) {
                settings.load(input);
            } catch (IOException exception) {
                System.err.println("Ошибка загрузки настроек: " + exception.getMessage());
                setDefaultSettings();
            }
        } else {
            setDefaultSettings();
        }
    }

    private static void setDefaultSettings() {
        settings.setProperty(CONFIRMATIONS_KEY, CONFIRMATIONS_ENABLED);
        settings.setProperty(FIRST_MOVE_KEY, FIRST_MOVE_RANDOM);
        settings.setProperty(BOT_THINKING_KEY, BOT_THINKING_ENABLED);
        saveSettings();
    }

    private static void saveSettings() {
        try (final OutputStream output = new FileOutputStream(FileConstants.SETTINGS_FILE)) {
            settings.store(output, "TicTacToe Settings");
        } catch (IOException exception) {
            System.err.println("Ошибка сохранения настроек: " + exception.getMessage());
        }
    }

    private static boolean isValidFirstMoveSetting(final String firstMove) {
        return FIRST_MOVE_X.equals(firstMove) || FIRST_MOVE_O.equals(firstMove) || FIRST_MOVE_RANDOM.equals(firstMove);
    }
}