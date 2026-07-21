package edu.austral.dissis.chess.rules.board;

import edu.austral.dissis.chess.model.pieces.King;
import edu.austral.dissis.common.model.Board;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Position;
import edu.austral.dissis.common.rules.board.BoardEffect;

public class CastlingBoardEffect implements BoardEffect {

    @Override
    public boolean appliesTo(Move move, GameState state) {
        return state.board().pieceAt(move.from())
                .filter(p -> p.isType(King.INSTANCE))
                .isPresent()
                && move.absColDiff() == 2
                && move.absRowDiff() == 0;
    }

    @Override
    public Board apply(Move move, GameState state, Board board) {
        boolean kingside = move.colDiff() > 0;
        int row = move.from().row();
        Position rookFrom = new Position(row, kingside ? 7 : 0);
        Position rookTo = new Position(row, kingside ? 5 : 3);
        return board.boardWithMove(rookFrom, rookTo);
    }
}
