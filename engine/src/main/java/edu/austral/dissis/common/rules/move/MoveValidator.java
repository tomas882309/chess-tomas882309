package edu.austral.dissis.common.rules.move;

import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;

import java.util.Optional;

public interface MoveValidator {
    Optional<String> findViolation(Move move, GameState state);
}
