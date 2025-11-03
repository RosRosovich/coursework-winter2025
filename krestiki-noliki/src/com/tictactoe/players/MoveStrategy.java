package com.tictactoe.players;

import com.tictactoe.core.Board;

public interface MoveStrategy {
    boolean makeMove(Board board, Player player);
}