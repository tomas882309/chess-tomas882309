package edu.austral.dissis.chess.behaviour;

import edu.austral.dissis.common.behaviour.MoveBehaviour;
import edu.austral.dissis.common.model.*;

public class ChancellorMoveBehaviour implements MoveBehaviour {
    private final RookMoveBehaviour rook = new RookMoveBehaviour();
    private final KnightMoveBehaviour knight = new KnightMoveBehaviour();

    @Override
    public boolean isValidMove(Move move, Piece piece, GameState state) {
        return rook.isValidMove(move, piece, state) || knight.isValidMove(move, piece, state);
    }
}