package com.tictactoe.core;

import com.tictactoe.players.Player;

public interface FirstMoveStrategy {
    Player determineFirstPlayer(Player player1, Player player2);
}