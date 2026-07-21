package edu.austral.dissis.chess.rules.board;

import edu.austral.dissis.chess.model.EnPassantExtraState;
import edu.austral.dissis.chess.model.pieces.Pawn;
import edu.austral.dissis.common.model.Board;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Position;
import edu.austral.dissis.common.rules.board.BoardEffect;

public class EnPassantBoardEffect implements BoardEffect {
    @Override
    public boolean appliesTo(Move move, GameState state) {
        return state.board().pieceAt(move.from())
                .filter(p -> p.isType(Pawn.INSTANCE))
                .isPresent()
                && state.board().pieceAt(move.to()).isEmpty()
                && move.absColDiff() == 1
                && state.extraState().get(EnPassantExtraState.class)
                .flatMap(EnPassantExtraState::target)
                .map(t -> t.equals(move.to()))
                .orElse(false);
    }

    @Override
    public Board apply(Move move, GameState state, Board board) {
        return board.withoutPieceAt(new Position(move.from().row(), move.to().col()));
    }

}
