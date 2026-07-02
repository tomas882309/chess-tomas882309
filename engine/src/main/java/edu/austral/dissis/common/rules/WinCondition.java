package edu.austral.dissis.common.rules;

import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.GameStatus;
import edu.austral.dissis.common.model.Move;

public interface WinCondition {
  GameStatus evaluate(GameState state, Move lastMove);
}
