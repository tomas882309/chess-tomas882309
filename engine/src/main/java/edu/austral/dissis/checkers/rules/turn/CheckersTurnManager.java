package edu.austral.dissis.checkers.rules.turn;

import edu.austral.dissis.checkers.model.MultiCaptureExtraState;
import edu.austral.dissis.checkers.rules.CaptureChecker;
import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.rules.turn.TurnManager;

public class CheckersTurnManager implements TurnManager {

    @Override
    public Color nextPlayer(Color current, GameState state) {
        boolean multiCaptureOngoing = state.extraState()
                .get(MultiCaptureExtraState.class)
                .flatMap(MultiCaptureExtraState::lastCapturingPiece)
                .filter(pos -> CaptureChecker.canCaptureFrom(pos, state))
                .isPresent();
        return multiCaptureOngoing ? current : (current == Color.WHITE ? Color.BLACK : Color.WHITE);
    }
}