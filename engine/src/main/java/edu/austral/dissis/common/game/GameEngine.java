package edu.austral.dissis.common.game;

import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.MoveResult;

public interface GameEngine {
  MoveResult executeMove(Move move, GameState state);
}
