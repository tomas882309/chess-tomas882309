package edu.austral.dissis.common.rules;

import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.GameState;

public interface TurnManager {
  Color nextPlayer(Color current, GameState state);
}
