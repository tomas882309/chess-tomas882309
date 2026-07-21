package edu.austral.dissis.checkers.rules.move;

import edu.austral.dissis.checkers.rules.CaptureChecker;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.rules.move.MoveValidator;

import java.util.Optional;

public class MandatoryCaptureValidator implements MoveValidator {

    @Override
    public Optional<String> findViolation(Move move, GameState state) {
        if (isCapture(move)) return Optional.empty();
        if (CaptureChecker.anyCaptureAvailable(state.currentPlayer(), state))
            return Optional.of("Hay una captura disponible, debés capturar");
        return Optional.empty();
    }

    private boolean isCapture(Move move) {
        return move.absRowDiff() >= 2 && move.absRowDiff() == move.absColDiff();
    }
}