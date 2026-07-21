package edu.austral.dissis.chess.behaviour;

import edu.austral.dissis.common.behaviour.MoveBehaviour;
import edu.austral.dissis.common.model.*;

public class ArchbishopMoveBehaviour implements MoveBehaviour {
    private final BishopMoveBehaviour bishop = new BishopMoveBehaviour();
    private final KnightMoveBehaviour knight = new KnightMoveBehaviour();

    @Override
    public boolean isValidMove(Move move, Piece piece, GameState state) {
        return bishop.isValidMove(move, piece, state) || knight.isValidMove(move, piece, state);
    }
}