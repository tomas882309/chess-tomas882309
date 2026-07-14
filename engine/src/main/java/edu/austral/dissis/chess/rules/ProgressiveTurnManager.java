package edu.austral.dissis.chess.rules;

import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.rules.TurnManager;

public class ProgressiveTurnManager implements TurnManager {

  private int movesInRound = 0;
  private int roundSize = 1;

  @Override
  public Color nextPlayer(Color current, GameState state) {
    movesInRound++;
    if (movesInRound >= roundSize) {
      movesInRound = 0;
      roundSize++;
      return current == Color.WHITE ? Color.BLACK : Color.WHITE;
    }
    return current;
  }
}
