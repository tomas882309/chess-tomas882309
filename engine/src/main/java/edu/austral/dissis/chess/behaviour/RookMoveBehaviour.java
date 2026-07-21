package edu.austral.dissis.chess.behaviour;

import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Piece;
import edu.austral.dissis.common.behaviour.MoveBehaviour;

public class RookMoveBehaviour implements MoveBehaviour {

    private final SlidingMoveHelper helper = new SlidingMoveHelper();

    @Override
    public boolean isValidMove(Move move, Piece piece, GameState state) {
        if (move.absRowDiff() == 0 && move.absColDiff() == 0) {
            return false;
        }
        if (move.rowDiff() != 0 && move.colDiff() != 0) {
            return false;
        }
        return helper.isPathClearAndDestinationFree(move, piece, state.board());
    }
}
