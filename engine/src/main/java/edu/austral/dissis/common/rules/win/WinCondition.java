package edu.austral.dissis.common.rules.win;

import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.GameStatus;

public interface WinCondition {
    GameStatus evaluate(GameState state);
}
