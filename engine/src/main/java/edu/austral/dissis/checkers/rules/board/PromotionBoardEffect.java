package edu.austral.dissis.checkers.rules.board;

import edu.austral.dissis.checkers.factory.CheckersPieceFactory;
import edu.austral.dissis.checkers.model.pieces.Man;
import edu.austral.dissis.common.model.Board;
import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.rules.board.BoardEffect;

public class PromotionBoardEffect implements BoardEffect {

    @Override
    public boolean appliesTo(Move move, GameState state) {
        int lastRow = state.currentPlayer() == Color.WHITE ? 0 : 7;
        return state.board().pieceAt(move.from())
                .filter(p -> p.isType(Man.INSTANCE))
                .isPresent()
                && move.to().row() == lastRow;
    }

    @Override
    public Board apply(Move move, GameState state, Board board) {
        return board.withPieceAt(move.to(), CheckersPieceFactory.king(state.currentPlayer()));
    }
}