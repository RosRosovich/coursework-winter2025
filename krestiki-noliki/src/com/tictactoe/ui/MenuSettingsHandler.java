package com.tictactoe.ui;

import com.tictactoe.storage.SettingsManager;

import java.util.Objects;
import java.util.Scanner;

public final class MenuSettingsHandler {
    private static final String FIRST_MOVE_X = "X";
    private static final String FIRST_MOVE_O = "O";
    private static final String FIRST_MOVE_RANDOM = "random";

    private MenuSettingsHandler() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    public static void showSettings(final Scanner scanner) {
        Objects.requireNonNull(scanner, "Scanner cannot be null");

        while (true) {
            System.out.println("\n=== Настройки ===");
            System.out.println("1. Подтверждения: " + getConfirmationsStatus());
            System.out.println("2. Первый ход: " + getFirstMoveStatus());
            System.out.println("3. Размышление бота: " + getBotThinkingStatus());
            System.out.println("0. Назад");
            System.out.print("Выберите настройку: ");

            final String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> toggleConfirmations();
                case "2" -> changeFirstMove(scanner);
                case "3" -> toggleBotThinking();
                case "0" -> { return; }
                default -> System.out.println("Неверный ввод.");
            }
        }
    }

    private static String getConfirmationsStatus() {
        return SettingsManager.isConfirmationsEnabled() ? "✅ ВКЛ" : "❌ ВЫКЛ";
    }

    private static String getFirstMoveStatus() {
        final String setting = SettingsManager.getFirstMove();
        return switch (setting) {
            case FIRST_MOVE_X -> "✅ X";
            case FIRST_MOVE_O -> "✅ O";
            default -> "✅ Бросок кубика";
        };
    }

    private static String getBotThinkingStatus() {
        return SettingsManager.isBotThinkingEnabled() ? "✅ ВКЛ" : "❌ ВЫКЛ";
    }

    private static void toggleConfirmations() {
        final boolean current = SettingsManager.isConfirmationsEnabled();
        SettingsManager.setConfirmationsEnabled(!current);
        System.out.println("Подтверждения " + (current ? "❌ ВЫКЛЮЧЕНЫ" : "✅ ВКЛЮЧЕНЫ"));
    }

    private static void toggleBotThinking() {
        final boolean current = SettingsManager.isBotThinkingEnabled();
        SettingsManager.setBotThinkingEnabled(!current);
        System.out.println("Размышление бота " + (current ? "❌ ВЫКЛЮЧЕНО" : "✅ ВКЛЮЧЕНО"));
    }

    private static void changeFirstMove(final Scanner scanner) {
        Objects.requireNonNull(scanner, "Scanner cannot be null");

        System.out.println("\nВыберите кто ходит первым:");
        System.out.println("1. X " + (FIRST_MOVE_X.equals(SettingsManager.getFirstMove()) ? "✅" : ""));
        System.out.println("2. O " + (FIRST_MOVE_O.equals(SettingsManager.getFirstMove()) ? "✅" : ""));
        System.out.println("3. Бросок кубика " + (FIRST_MOVE_RANDOM.equals(SettingsManager.getFirstMove()) ? "✅" : ""));
        System.out.print("Выберите вариант: ");

        final String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> {
                SettingsManager.setFirstMove(FIRST_MOVE_X);
                System.out.println("✅ Первым всегда ходит X");
            }
            case "2" -> {
                SettingsManager.setFirstMove(FIRST_MOVE_O);
                System.out.println("✅ Первым всегда ходит O");
            }
            case "3" -> {
                SettingsManager.setFirstMove(FIRST_MOVE_RANDOM);
                System.out.println("✅ Первый ход определяется броском кубика");
            }
            default -> System.out.println("Неверный выбор.");
        }
    }
}