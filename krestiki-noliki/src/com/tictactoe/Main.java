package com.tictactoe;

import com.tictactoe.ui.Menu;

public final class Main {
    public static void main(String[] args) {
        try {
            Menu menu = new Menu();
            menu.showMainMenu();
        } catch (Exception exception) {
            System.err.println("Критическая ошибка при запуске приложения: " + exception.getMessage());
            System.err.println("Тип ошибки: " + exception.getClass().getSimpleName());
            System.exit(1);
        }
    }
}