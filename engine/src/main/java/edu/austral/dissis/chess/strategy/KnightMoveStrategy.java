package edu.austral.dissis.chess.strategy;

import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Piece;
import edu.austral.dissis.common.rules.MoveStrategy;

public class KnightMoveStrategy implements MoveStrategy {

  @Override
  public boolean isValidMove(Move move, Piece piece, GameState state) {
    int dr = Math.abs(move.to().row() - move.from().row());
    int dc = Math.abs(move.to().col() - move.from().col());
    boolean isLshape = (dr == 2 && dc == 1) || (dr == 1 && dc == 2);
    return isLshape && !state.board().isOccupiedByColor(move.to(), piece.color());
  }
}
