package com.tictactoe.ui;

import com.tictactoe.players.Player;
import com.tictactoe.storage.PlayerManager;

import java.util.Objects;
import java.util.Scanner;

public final class Menu {
    private final Scanner scanner;
    private Player currentPlayer;

    public Menu() {
        this.scanner = new Scanner(System.in);
    }

    public void showMainMenu() {
        while (true) {
            displayMainMenu();
            final String choice = scanner.nextLine().trim();

            if (!processMainMenuChoice(choice)) {
                return;
            }
        }
    }

    private void displayMainMenu() {
        System.out.println("\n=== Крестики-нолики ===");
        System.out.println("1. Игроки");
        System.out.println("2. Игра с ботом");
        System.out.println("3. Игра с самим собой");
        System.out.println("4. Цвета");
        System.out.println("5. Настройки");
        System.out.println("6. Правила игры");
        if (currentPlayer != null) {
            System.out.println("7. Информация о текущем игроке");
        }
        System.out.println("0. Выход");
        System.out.print("Выбор: ");
    }

    private boolean processMainMenuChoice(final String choice) {
        switch (choice) {
            case "1" -> showPlayerMenu();
            case "2" -> MenuGameHandler.startBotGame(scanner, currentPlayer);
            case "3" -> MenuGameHandler.startSelfGame(scanner, currentPlayer);
            case "4" -> MenuColorHandler.manageColors(scanner, currentPlayer);
            case "5" -> MenuSettingsHandler.showSettings(scanner);
            case "6" -> showGameRules();
            case "7" -> showCurrentPlayerInfo();
            case "0" -> {
                System.out.println("До свидания!");
                return false;
            }
            default -> System.out.println("Неверный ввод.");
        }
        return true;
    }

    private void showPlayerMenu() {
        while (true) {
            displayPlayerMenu();
            final String choice = scanner.nextLine().trim();

            if (!processPlayerMenuChoice(choice)) {
                return;
            }
        }
    }

    private void displayPlayerMenu() {
        System.out.println("\n=== Управление игроками ===");
        System.out.println("1. Создать игрока");
        System.out.println("2. Загрузить игрока");
        System.out.println("3. Удалить игрока");
        if (currentPlayer != null) {
            System.out.println("4. Текущий игрок: " + currentPlayer.getName() + " (" + currentPlayer.getSymbol() + ")");
        }
        System.out.println("0. Назад");
        System.out.print("Выбор: ");
    }

    private boolean processPlayerMenuChoice(final String choice) {
        switch (choice) {
            case "1" -> MenuPlayerHandler.createPlayer(scanner, this::setCurrentPlayer);
            case "2" -> MenuPlayerHandler.loadPlayer(scanner, this::setCurrentPlayer);
            case "3" -> MenuPlayerHandler.deletePlayer(scanner, currentPlayer, this::clearCurrentPlayer);
            case "4" -> showCurrentPlayerInfo();
            case "0" -> { return false; }
            default -> System.out.println("Неверный ввод.");
        }
        return true;
    }

    private void showGameRules() {
        System.out.println("\n=== ПРАВИЛА ИГРЫ ===");
        System.out.println("🎯 Основные правила:");
        System.out.println("- Игроки по очереди ставят крестики (X) и нолики (O) на поле 3x3");
        System.out.println("- Первый, выстроивший 3 своих фигуры в ряд (по горизонтали,");
        System.out.println("вертикали или диагонали) - побеждает!");
        System.out.println("- Если все клетки заполнены, но победителя нет - ничья");

        System.out.println("\n🎮 Режимы игры:");
        System.out.println("- Игра с ботом - сразитесь против компьютерного противника");
        System.out.println("- Игра с самим собой - управляйте двумя игроками по очереди");

        System.out.println("\n⚡ Особенности:");
        System.out.println("- Обычный бот - ходит случайным образом");
        System.out.println("- Хардкор-бот - может ЗАМЕНЯТЬ ваши фигуры своими!");
        System.out.println("- Хардкор режим доступен после 10 побед над обычным ботом");

        System.out.println("\n🎨 Система цветов:");
        System.out.println("- Цвета разблокируются за достижения (победы и поражения)");
        System.out.println("- Каждый цвет требует определенного количества игр");
        System.out.println("- Используйте цвета для персонализации своего имени");

        System.out.println("\n⭐ Достижения:");
        System.out.println("- Победы открывают новые цвета");
        System.out.println("- Даже поражения приносят награды");
        System.out.println("- Следите за статистикой в профиле игрока");

        System.out.println("\n🎲 Первый ход:");
        System.out.println("- Определяется броском кубика (по умолчанию)");
        System.out.println("- Можно настроить, чтобы всегда ходил X или O");
        System.out.println("- В хардкоре первый ход всегда случайный");

        System.out.println("\nНажмите Enter чтобы продолжить...");
        scanner.nextLine();
    }

    private void showCurrentPlayerInfo() {
        if (currentPlayer != null) {
            PlayerManager.displayPlayerInfo(currentPlayer);
        } else {
            System.out.println("Сначала выберите игрока.");
        }
    }

    private void setCurrentPlayer(final Player player) {
        this.currentPlayer = Objects.requireNonNull(player, "Player cannot be null");
    }

    private void clearCurrentPlayer() {
        this.currentPlayer = null;
    }
}