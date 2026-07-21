package edu.austral.dissis.chess.behaviour;

import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Piece;
import edu.austral.dissis.common.behaviour.MoveBehaviour;

public class KnightMoveBehaviour implements MoveBehaviour {
    @Override
    public boolean isValidMove(Move move, Piece piece, GameState state) {
        boolean isLShape = (move.absRowDiff() == 2 && move.absColDiff() == 1) || (move.absRowDiff() == 1 && move.absColDiff() == 2);

        return isLShape && !state.board().isOccupiedByColor(move.to(), piece.color());
    }
}
