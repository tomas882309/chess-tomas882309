package edu.austral.dissis.chess.model;

import edu.austral.dissis.chess.model.pieces.King;
import edu.austral.dissis.chess.model.pieces.Rook;
import edu.austral.dissis.common.model.state.GameExtraState;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Piece;

public record CastlingExtraState(CastlingRights rights) implements GameExtraState {

    @Override
    public GameExtraState update(Move move, GameState state) {
        return state.board().pieceAt(move.from())
                .map(piece -> computeNewRights(move, piece))
                .map(CastlingExtraState::new)
                .orElse(this);
    }

    private CastlingRights computeNewRights(Move move, Piece piece) {
        if (piece.isType(King.INSTANCE))
            return rights.revokeAll(piece.color());
        if (piece.isType(Rook.INSTANCE))
            return move.from().col() == 0
                    ? rights.revokeQueenside(piece.color())
                    : rights.revokeKingside(piece.color());
        return rights;
    }
}
