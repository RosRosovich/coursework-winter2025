package com.tictactoe.ui;

import com.tictactoe.players.Player;
import com.tictactoe.storage.ColorManager;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public final class MenuColorHandler {
    private static final String RANDOM_COMMAND = "random";

    private MenuColorHandler() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    public static void manageColors(final Scanner scanner, final Player currentPlayer) {
        Objects.requireNonNull(scanner, "Scanner cannot be null");

        if (currentPlayer == null) {
            System.out.println("Сначала загрузите игрока.");
            return;
        }

        while (true) {
            System.out.println("\n=== Меню цветов ===");
            System.out.println("1. Мои цвета (только доступные)");
            System.out.println("2. Все цвета (и условия)");
            System.out.println("3. Надеть цвет (или random)");
            System.out.println("4. Промокод");
            System.out.println("0. Назад");
            System.out.print("Выбор: ");

            final String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> ColorManager.showMyColors(currentPlayer);
                case "2" -> ColorManager.showAllColors(currentPlayer);
                case "3" -> handleColorEquip(scanner, currentPlayer);
                case "4" -> enterPromoCode(scanner, currentPlayer);
                case "0" -> { return; }
                default -> System.out.println("Неверный ввод.");
            }
        }
    }

    private static void enterPromoCode(final Scanner scanner, final Player currentPlayer) {
        Objects.requireNonNull(scanner, "Scanner cannot be null");
        Objects.requireNonNull(currentPlayer, "Current player cannot be null");

        System.out.print("Введите промокод: ");
        final String code = scanner.nextLine().trim();

        if (code.isEmpty()) {
            System.out.println("❌ Промокод не может быть пустым.");
            return;
        }

        ColorManager.applyPromoCode(currentPlayer, code);
    }

    private static void handleColorEquip(final Scanner scanner, final Player currentPlayer) {
        Objects.requireNonNull(scanner, "Scanner cannot be null");
        Objects.requireNonNull(currentPlayer, "Current player cannot be null");

        final List<String> availableColors = ColorManager.getAvailableColorKeys(currentPlayer.getName());
        final String equippedColor = ColorManager.getPlayerColor(currentPlayer.getName());

        if (availableColors.isEmpty()) {
            System.out.println("❌ Нет доступных цветов для экипировки.");
            return;
        }

        displayAvailableColors(availableColors, equippedColor);
        processUserInput(scanner, availableColors, currentPlayer.getName());
    }

    private static void displayAvailableColors(final List<String> availableColors, final String equippedColor) {
        System.out.println("\nДоступные цвета:");
        for (int i = 0; i < availableColors.size(); i++) {
            final String colorKey = availableColors.get(i);
            final String displayName = ColorManager.getDisplayName(colorKey);
            final String equippedMarker = colorKey.equals(equippedColor) ? " (надет)" : "";
            System.out.println((i + 1) + ". " + displayName + equippedMarker);
        }
    }

    private static void processUserInput(final Scanner scanner, final List<String> availableColors, final String playerName) {
        System.out.print("Введите номер цвета, ключ цвета или 'random': ");
        final String input = scanner.nextLine().trim().toLowerCase();

        if (input.isEmpty()) {
            System.out.println("❌ Ввод не может быть пустым.");
            return;
        }

        if (input.equals(RANDOM_COMMAND)) {
            ColorManager.equipRandomColorForPlayer(playerName);
            return;
        }

        try {
            final int colorIndex = Integer.parseInt(input);
            if (colorIndex >= 1 && colorIndex <= availableColors.size()) {
                final String selectedColor = availableColors.get(colorIndex - 1);
                ColorManager.equipColor(playerName, selectedColor);
            } else {
                System.out.println("❌ Неверный номер. Доступные номера: 1-" + availableColors.size());
            }
            return;
        } catch (NumberFormatException exception) {
            // Если не число, пробуем как ключ цвета
        }

        if (availableColors.contains(input)) {
            ColorManager.equipColor(playerName, input);
        } else {
            System.out.println("❌ Цвет '" + input + "' не найден. Используйте номер из списка или правильный ключ цвета.");
        }
    }
}