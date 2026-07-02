package edu.austral.dissis.common.rules;

import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.GameState;

public class StandardTurnManager implements TurnManager {

  @Override
  public Color nextPlayer(Color current, GameState state) {
    return current.opposite();
  }
}
