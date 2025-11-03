package com.tictactoe.ui;

import com.tictactoe.core.GameConstants;
import com.tictactoe.players.HumanPlayer;
import com.tictactoe.players.Player;
import com.tictactoe.storage.FileConstants;
import com.tictactoe.storage.PlayerManager;
import com.tictactoe.storage.SettingsManager;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Consumer;

public final class MenuPlayerHandler {
    private static final String CONFIRMATION_POSITIVE = "+";
    private static final String CHOICE_BY_NUMBER = "1";
    private static final String CHOICE_BY_NAME = "2";

    private MenuPlayerHandler() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    public static void createPlayer(final Scanner scanner, final Consumer<Player> setCurrentPlayer) {
        Objects.requireNonNull(scanner, "Scanner cannot be null");
        Objects.requireNonNull(setCurrentPlayer, "SetCurrentPlayer callback cannot be null");

        while (true) {
            System.out.print("Введите имя игрока: ");
            final String name = scanner.nextLine().trim();

            if (name.isEmpty()) {
                System.out.println("❌ Имя игрока не может быть пустым!");
                continue;
            }

            if (!isValidFileName(name)) {
                System.out.println("❌ Имя содержит недопустимые символы для имени файла!");
                System.out.println("❌ Нельзя использовать: \\ / : * ? \" < > |");
                final String suggestedName = sanitizeFileName(name);
                System.out.println("💡 Попробуйте использовать: " + suggestedName);
                continue;
            }

            if (PlayerManager.playerExists(name)) {
                System.out.println("❌ Игрок с таким именем уже существует!");
                continue;
            }

            final char symbol = selectPlayerSymbol(scanner);
            if (symbol == ' ') {
                return;
            }

            if (confirmAction(scanner, "Подтвердить создание игрока " + name)) {
                final Player player = new HumanPlayer(name, symbol, scanner);
                try {
                    PlayerManager.savePlayer(player);
                    setCurrentPlayer.accept(player);
                    System.out.println("✅ Игрок " + name + " создан.");
                } catch (Exception exception) {
                    System.err.println("❌ Ошибка создания игрока: " + exception.getMessage());
                    System.out.println("💡 Попробуйте использовать другое имя.");
                    continue;
                }
            } else {
                System.out.println("Создание отменено.");
            }
            break;
        }
    }

    public static void loadPlayer(final Scanner scanner, final Consumer<Player> setCurrentPlayer) {
        Objects.requireNonNull(scanner, "Scanner cannot be null");
        Objects.requireNonNull(setCurrentPlayer, "SetCurrentPlayer callback cannot be null");

        final List<String> allPlayers = PlayerManager.listPlayers();
        if (allPlayers.isEmpty()) {
            System.out.println("Нет сохранённых игроков.");
            return;
        }

        displayPlayerList(allPlayers, "Доступные игроки:");

        System.out.print("Введите номер или имя игрока: ");
        final String input = scanner.nextLine().trim();
        final String playerName = findPlayerByNameOrIndex(allPlayers, input, scanner);

        if (playerName == null) {
            System.out.println("Игрок не найден.");
            return;
        }

        final Player player = PlayerManager.loadPlayer(playerName, GameConstants.PLAYER_X);
        if (player == null) {
            System.out.println("Игрок не найден (ошибка загрузки).");
            return;
        }

        if (confirmAction(scanner, "Подтвердить загрузку игрока " + playerName)) {
            setCurrentPlayer.accept(player);
            System.out.println("✅ Игрок " + playerName + " загружен.");
        } else {
            System.out.println("Загрузка отменена.");
        }
    }

    public static void deletePlayer(final Scanner scanner, final Player currentPlayer, final Runnable clearCurrentPlayer) {
        Objects.requireNonNull(scanner, "Scanner cannot be null");
        Objects.requireNonNull(clearCurrentPlayer, "ClearCurrentPlayer callback cannot be null");

        final List<String> allPlayers = PlayerManager.listPlayers();
        if (allPlayers.isEmpty()) {
            System.out.println("Нет игроков для удаления.");
            return;
        }

        displayPlayerList(allPlayers, "Игроки:");

        System.out.print("Введите номер или имя игрока для удаления: ");
        final String input = scanner.nextLine().trim();
        final String playerName = findPlayerByNameOrIndex(allPlayers, input, scanner);

        if (playerName == null) {
            System.out.println("Игрок не найден.");
            return;
        }

        if (confirmAction(scanner, "Удалить игрока " + playerName)) {
            final boolean deletionSuccessful = deletePlayerFiles(playerName);
            if (deletionSuccessful) {
                if (currentPlayer != null && currentPlayer.getName().equals(playerName)) {
                    clearCurrentPlayer.run();
                }
                System.out.println("🗑 Игрок " + playerName + " удалён.");
            } else {
                System.out.println("❌ Ошибка при удалении игрока.");
            }
        } else {
            System.out.println("Удаление отменено.");
        }
    }

    public static boolean confirmAction(final Scanner scanner, final String message) {
        Objects.requireNonNull(scanner, "Scanner cannot be null");
        Objects.requireNonNull(message, "Message cannot be null");

        if (!SettingsManager.isConfirmationsEnabled()) {
            return true;
        }
        System.out.print(message + " (+/-): ");
        final String confirmation = scanner.nextLine().trim();
        return confirmation.equals(CONFIRMATION_POSITIVE);
    }

    public static void displayPlayerList(final List<String> players, final String title) {
        Objects.requireNonNull(players, "Players list cannot be null");
        Objects.requireNonNull(title, "Title cannot be null");

        System.out.println("\n" + title);
        if (players.isEmpty()) {
            System.out.println("Нет доступных игроков.");
            return;
        }

        for (int i = 0; i < players.size(); i++) {
            System.out.println((i + 1) + ". " + players.get(i));
        }
    }

    public static String findPlayerByNameOrIndex(final List<String> players, final String input, final Scanner scanner) {
        Objects.requireNonNull(players, "Players list cannot be null");
        Objects.requireNonNull(input, "Input cannot be null");
        Objects.requireNonNull(scanner, "Scanner cannot be null");

        if (input.isEmpty()) {
            System.out.println("❌ Ввод не может быть пустым.");
            return null;
        }

        try {
            final int inputAsNumber = Integer.parseInt(input);
            return handleNumericInput(players, input, inputAsNumber, scanner);
        } catch (NumberFormatException exception) {
            return handleStringInput(players, input);
        }
    }

    private static String handleNumericInput(final List<String> players, final String input,
                                             final int inputAsNumber, final Scanner scanner) {
        final boolean existsAsIndex = (inputAsNumber >= 1 && inputAsNumber <= players.size());
        final boolean existsAsName = players.contains(input);
        final boolean numberMatchesName = existsAsIndex && players.get(inputAsNumber - 1).equals(input);

        if (numberMatchesName) {
            return players.get(inputAsNumber - 1);
        } else if (existsAsName && existsAsIndex) {
            return resolveAmbiguousInput(players, input, inputAsNumber, scanner);
        } else if (existsAsIndex) {
            return players.get(inputAsNumber - 1);
        } else if (existsAsName) {
            return input;
        } else {
            System.out.println("❌ Неверный номер. Доступные номера: 1-" + players.size());
            return null;
        }
    }

    private static String resolveAmbiguousInput(final List<String> players, final String input,
                                                final int inputAsNumber, final Scanner scanner) {
        System.out.println("⚠️  Найдено несколько совпадений:");
        System.out.println("По номеру " + inputAsNumber + ": " + players.get(inputAsNumber - 1));
        System.out.println("По имени '" + input + "': " + input);
        System.out.print("Выберите вариант (1 - по номеру, 2 - по имени): ");

        final String choice = scanner.nextLine().trim();
        if (choice.equals(CHOICE_BY_NUMBER)) {
            return players.get(inputAsNumber - 1);
        } else if (choice.equals(CHOICE_BY_NAME)) {
            return input;
        } else {
            System.out.println("❌ Неверный выбор.");
            return null;
        }
    }

    private static String handleStringInput(final List<String> players, final String input) {
        if (players.contains(input)) {
            return input;
        }

        System.out.println("❌ Игрок '" + input + "' не найден.");
        displayPlayerList(players, "Доступные игроки:");
        return null;
    }

    private static char selectPlayerSymbol(final Scanner scanner) {
        Objects.requireNonNull(scanner, "Scanner cannot be null");

        while (true) {
            System.out.print("Выберите символ (X/O): ");
            final String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("X") || input.equals("O")) {
                return input.charAt(0);
            } else if (input.isEmpty()) {
                System.out.println("❌ Символ не может быть пустым.");
            } else {
                System.out.println("❌ Ошибка. Нужно ввести X или O.");
            }
        }
    }

    private static boolean deletePlayerFiles(final String playerName) {
        Objects.requireNonNull(playerName, "Player name cannot be null");

        final File playerFile = new File(playerName + FileConstants.PLAYER_FILE_SUFFIX);
        return !playerFile.exists() || playerFile.delete();
    }

    private static boolean isValidFileName(final String name) {
        return !name.matches(".*[\\\\/:*?\"<>|].*");
    }

    private static String sanitizeFileName(final String name) {
        return name.replaceAll("[\\\\/:*?\"<>|]", "_");
    }
}