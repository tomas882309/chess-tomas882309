package edu.austral.dissis.common.behaviour;

import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Piece;

public interface MoveBehaviour {
    boolean isValidMove(Move move, Piece piece, GameState state);
}
