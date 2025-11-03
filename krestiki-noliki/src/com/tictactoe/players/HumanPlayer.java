package com.tictactoe.players;

import com.tictactoe.core.Board;

import java.util.Scanner;

public class HumanPlayer extends Player {
    private final MoveStrategy moveStrategy;

    public HumanPlayer(final String name, final char symbol, final Scanner scanner) {
        super(name, symbol);
        this.moveStrategy = new HumanMoveStrategy(scanner);
    }

    @Override
    public boolean makeMove(final Board board, final Scanner scanner) {
        return moveStrategy.makeMove(board, this);
    }
}