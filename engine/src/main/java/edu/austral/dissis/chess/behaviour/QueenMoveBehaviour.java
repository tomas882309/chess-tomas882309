package edu.austral.dissis.chess.behaviour;

import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Piece;
import edu.austral.dissis.common.behaviour.MoveBehaviour;

public class QueenMoveBehaviour implements MoveBehaviour {

    private final RookMoveBehaviour rook = new RookMoveBehaviour();
    private final BishopMoveBehaviour bishop = new BishopMoveBehaviour();

    @Override
    public boolean isValidMove(Move move, Piece piece, GameState state) {
        return rook.isValidMove(move, piece, state) || bishop.isValidMove(move, piece, state);
    }
}
