package edu.austral.dissis.common.rules;

import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Piece;

public interface MoveStrategy {
  boolean isValidMove(Move move, Piece piece, GameState state);
}
