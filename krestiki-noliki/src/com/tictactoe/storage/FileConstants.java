package com.tictactoe.storage;

public final class FileConstants {
    public static final String PLAYER_FILE_SUFFIX = "_player.txt";
    public static final String SETTINGS_FILE = "settings.properties";
    public static final String NAME_KEY = "name";
    public static final String SYMBOL_KEY = "symbol";
    public static final String WINS_KEY = "wins";
    public static final String LOSSES_KEY = "losses";
    public static final String DRAWS_KEY = "draws";
    public static final String EQUIPPED_KEY = "equipped";
    public static final String UNLOCKED_KEY = "unlocked";
    public static final String DEFAULT_VALUE = "0";

    private FileConstants() {
        throw new AssertionError("Cannot instantiate utility class");
    }
}