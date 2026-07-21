package edu.austral.dissis.common.model;

import edu.austral.dissis.common.model.state.GameExtraState;

public record GameState(Board board, Color currentPlayer, GameStatus status, GameExtraState extraState) {

    public boolean isOver() {
        return status != GameStatus.IN_PROGRESS;
    }
}
