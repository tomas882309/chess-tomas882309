package edu.austral.dissis.chess.strategy;

import edu.austral.dissis.common.model.Board;
import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Position;

public class SlidingMoveHelper {

  public boolean isPathClearAndDestinationFree(Move move, Color pieceColor, Board board) {
    if (board.isOccupiedByColor(move.to(), pieceColor)) {
      return false;
    }
    return isPathClear(move, board);
  }

  public boolean isPathClear(Move move, Board board) {
    int dr = Integer.signum(move.to().row() - move.from().row());
    int dc = Integer.signum(move.to().col() - move.from().col());
    Position current = move.from().offset(dr, dc);
    while (!current.equals(move.to())) {
      if (board.pieceAt(current).isPresent()) {
        return false;
      }
      current = current.offset(dr, dc);
    }
    return true;
  }
}
