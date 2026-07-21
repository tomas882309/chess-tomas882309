package edu.austral.dissis.checkers.rules.board;

import edu.austral.dissis.common.model.Board;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Position;
import edu.austral.dissis.common.rules.board.BoardEffect;

import java.util.Optional;

public class CaptureBoardEffect implements BoardEffect {

    @Override
    public boolean appliesTo(Move move, GameState state) {
        if (move.absRowDiff() != move.absColDiff() || move.absRowDiff() < 2) return false;
        return findCapturedPosition(move, state).isPresent();
    }

    @Override
    public Board apply(Move move, GameState state, Board board) {
        return findCapturedPosition(move, state)
                .map(board::withoutPieceAt)
                .orElse(board);
    }

    private Optional<Position> findCapturedPosition(Move move, GameState state) {
        int rowStep = move.rowDiff() > 0 ? 1 : -1;
        int colStep = move.colDiff() > 0 ? 1 : -1;
        int row = move.from().row() + rowStep;
        int col = move.from().col() + colStep;
        while (row != move.to().row()) {
            Position pos = new Position(row, col);
            if (state.board().pieceAt(pos)
                    .filter(p -> !p.isColor(state.currentPlayer()))
                    .isPresent())
                return Optional.of(pos);
            row += rowStep;
            col += colStep;
        }
        return Optional.empty();
    }
}