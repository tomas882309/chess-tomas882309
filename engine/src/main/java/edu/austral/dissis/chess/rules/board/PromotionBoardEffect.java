package edu.austral.dissis.chess.rules.board;

import edu.austral.dissis.chess.factory.ChessPieceFactory;
import edu.austral.dissis.chess.model.pieces.Pawn;
import edu.austral.dissis.common.model.*;
import edu.austral.dissis.common.rules.board.BoardEffect;

public class PromotionBoardEffect implements BoardEffect {

    @Override
    public boolean appliesTo(Move move, GameState state) {
        return state.board().pieceAt(move.from())
                .filter(p -> p.isType(Pawn.INSTANCE))
                .isPresent()
                && isPromotionRow(move, state);
    }

    @Override
    public Board apply(Move move, GameState state, Board board) {
        Piece queen = ChessPieceFactory.queen(state.currentPlayer());
        return board.withPieceAt(move.to(), queen);
    }

    private boolean isPromotionRow(Move move, GameState state) {
        int lastRow = state.currentPlayer() == Color.WHITE ? 7 : 0;
        return move.to().row() == lastRow;
    }
}
