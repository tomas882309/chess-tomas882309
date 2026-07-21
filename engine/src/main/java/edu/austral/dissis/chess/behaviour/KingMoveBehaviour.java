package edu.austral.dissis.chess.behaviour;

import edu.austral.dissis.chess.model.CastlingExtraState;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Piece;
import edu.austral.dissis.common.behaviour.MoveBehaviour;

public class KingMoveBehaviour implements MoveBehaviour {


    @Override
    public boolean isValidMove(Move move, Piece piece, GameState state) {
        return isNormalMove(move, piece, state) || isCastlingMove(move, piece, state);
    }

    private boolean isNormalMove(Move move, Piece piece, GameState state) {
        return move.absRowDiff() <= 1 && move.absColDiff() <= 1
                && (move.absRowDiff() + move.absColDiff() > 0)
                && !state.board().isOccupiedByColor(move.to(), piece.color());
    }

    private boolean isCastlingMove(Move move, Piece piece, GameState state) {
        if (move.absRowDiff() != 0 || move.absColDiff() != 2) return false;
        return state.extraState().get(CastlingExtraState.class)
                .map(c -> move.colDiff() > 0
                        ? c.rights().canCastleKingside(piece.color())
                        : c.rights().canCastleQueenside(piece.color()))
                .orElse(false);
    }
}
