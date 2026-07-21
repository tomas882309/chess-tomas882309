package edu.austral.dissis.checkers.rules.move;

import edu.austral.dissis.checkers.model.MultiCaptureExtraState;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Position;
import edu.austral.dissis.common.rules.move.MoveValidator;

import java.util.Optional;

public class MultiCaptureOnlyValidator implements MoveValidator {

    @Override
    public Optional<String> findViolation(Move move, GameState state) {
        Optional<Position> capturingPos = state.extraState()
                .get(MultiCaptureExtraState.class)
                .flatMap(MultiCaptureExtraState::lastCapturingPiece)
                .filter(pos -> state.board().pieceAt(pos)
                        .filter(p -> p.isColor(state.currentPlayer()))
                        .isPresent());

        if (capturingPos.isEmpty()) return Optional.empty();

        Position pos = capturingPos.get();
        if (!pos.equals(move.from()))
            return Optional.of("Debés continuar capturando con la misma pieza");
        if (move.absRowDiff() < 2 || move.absRowDiff() != move.absColDiff())
            return Optional.of("Debés continuar capturando");
        if (!hasOpponentOnPath(move, state))
            return Optional.of("Debés continuar capturando");
        return Optional.empty();
    }

    private boolean hasOpponentOnPath(Move move, GameState state) {
        int rowStep = move.rowDiff() > 0 ? 1 : -1;
        int colStep = move.colDiff() > 0 ? 1 : -1;
        int row = move.from().row() + rowStep;
        int col = move.from().col() + colStep;
        while (row != move.to().row()) {
            if (state.board().pieceAt(new Position(row, col))
                    .filter(p -> !p.isColor(state.currentPlayer()))
                    .isPresent()) return true;
            row += rowStep;
            col += colStep;
        }
        return false;
    }
}