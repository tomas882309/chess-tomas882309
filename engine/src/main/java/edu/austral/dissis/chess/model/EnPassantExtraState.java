package edu.austral.dissis.chess.model;

import edu.austral.dissis.chess.model.pieces.Pawn;
import edu.austral.dissis.common.model.state.GameExtraState;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Position;

import java.util.Optional;

public record EnPassantExtraState(Optional<Position> target) implements GameExtraState {
    @Override
    public GameExtraState update(Move move, GameState state) {
        boolean isDoublePawnPush = state.board().pieceAt(move.from())
                .filter(piece -> piece.isType(Pawn.INSTANCE))
                .isPresent()
                && move.absRowDiff() == 2 && move.absColDiff() == 0;

                if(!isDoublePawnPush) {
                    return new EnPassantExtraState(Optional.empty());
                }

                int targetRow = (move.from().row() + move.to().row()) / 2;
                return new EnPassantExtraState(Optional.of(new Position(targetRow, move.from().col())));
    }
}
