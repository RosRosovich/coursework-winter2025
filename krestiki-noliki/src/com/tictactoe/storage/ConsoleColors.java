package com.tictactoe.storage;

public final class ConsoleColors {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String BLUE = "\u001B[34m";
    public static final String CYAN = "\u001B[36m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String YELLOW = "\u001B[33m";
    public static final String WHITE = "\u001B[37m";
    public static final String LIME = "\u001B[92m";
    public static final String PINK = "\u001B[95m";
    public static final String SEA = "\u001B[96m";
    public static final String DARK_GREEN = "\u001B[32;1m";
    public static final String VIOLET = "\u001B[38;5;93m";
    public static final String CRIMSON = "\u001B[38;5;197m";

    private ConsoleColors() {
        throw new AssertionError("Cannot instantiate utility class");
    }
}