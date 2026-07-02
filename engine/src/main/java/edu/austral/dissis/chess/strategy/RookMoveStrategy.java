package edu.austral.dissis.chess.strategy;

import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Piece;
import edu.austral.dissis.common.rules.MoveStrategy;

public class RookMoveStrategy implements MoveStrategy {

  private final SlidingMoveHelper helper = new SlidingMoveHelper();

  @Override
  public boolean isValidMove(Move move, Piece piece, GameState state) {
    int dr = move.to().row() - move.from().row();
    int dc = move.to().col() - move.from().col();
    if (dr != 0 && dc != 0) {
      return false;
    }
    return helper.isPathClearAndDestinationFree(move, piece.color(), state.board());
  }
}
