package edu.austral.dissis.checkers.model;

import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Position;
import edu.austral.dissis.common.model.state.GameExtraState;

import java.util.Optional;

public record MultiCaptureExtraState(Optional<Position> lastCapturingPiece) implements GameExtraState {

    @Override
    public GameExtraState update(Move move, GameState state) {
        boolean wasCapture = wasCapture(move, state);
        return new MultiCaptureExtraState(wasCapture ? Optional.of(move.to()) : Optional.empty());
    }

    private boolean wasCapture(Move move, GameState state) {
        if (move.absRowDiff() != move.absColDiff() || move.absRowDiff() < 2) return false;
        int rowStep = move.rowDiff() > 0 ? 1 : -1;
        int colStep = move.colDiff() > 0 ? 1 : -1;
        int row = move.from().row() + rowStep;
        int col = move.from().col() + colStep;
        while (row != move.to().row()) {
            Position pos = new Position(row, col);
            if (state.board().pieceAt(pos)
                    .filter(p -> !p.isColor(state.currentPlayer()))
                    .isPresent())
                return true;
            row += rowStep;
            col += colStep;
        }
        return false;
    }
}