package com.tictactoe.storage;

public enum Color {
    GREEN("green", "Зелёный", ConsoleColors.GREEN),
    BLUE("blue", "Синий", ConsoleColors.BLUE),
    TEAL("teal", "Морской", ConsoleColors.SEA),
    DARK_GREEN("dark_green", "Тёмно-зелёный", ConsoleColors.DARK_GREEN),
    VIOLET("violet", "Сиреневый", ConsoleColors.VIOLET),
    RED("red", "Красный", ConsoleColors.RED),
    PURPLE("purple", "Фиолетовый", ConsoleColors.MAGENTA),
    PINK("pink", "Розовый", ConsoleColors.PINK),
    YELLOW("yellow", "Жёлтый", ConsoleColors.YELLOW),
    CRIMSON("crimson", "Малиновый", ConsoleColors.CRIMSON),
    CYAN("cyan", "Голубой (секрет)", ConsoleColors.CYAN),
    WHITE("white", "Белый", ConsoleColors.WHITE);

    private final String key;
    private final String displayName;
    private final String colorCode;

    Color(String key, String displayName, String colorCode) {
        this.key = key;
        this.displayName = displayName;
        this.colorCode = colorCode;
    }

    public static Color fromKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            return WHITE;
        }

        for (Color color : values()) {
            if (color.key.equals(key)) {
                return color;
            }
        }
        return WHITE;
    }

    public String getKey() {
        return key;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColorCode() {
        return colorCode;
    }
}