package edu.austral.dissis.checkers.strategy;

import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Piece;
import edu.austral.dissis.common.model.Position;
import edu.austral.dissis.common.rules.MoveStrategy;

public class CheckersKingMoveStrategy implements MoveStrategy {

  @Override
  public boolean isValidMove(Move move, Piece piece, GameState state) {
    int dr = move.to().row() - move.from().row();
    int dc = move.to().col() - move.from().col();

    if (Math.abs(dr) != Math.abs(dc) || dr == 0) {
      return false;
    }
    if (!state.board().isWithinBounds(move.to())) {
      return false;
    }
    if (state.board().pieceAt(move.to()).isPresent()) {
      return false;
    }

    int stepR = Integer.signum(dr);
    int stepC = Integer.signum(dc);
    int dist = Math.abs(dr);

    boolean foundOpponent = false;
    for (int i = 1; i < dist; i++) {
      Position cur = move.from().offset(i * stepR, i * stepC);
      var pieceOpt = state.board().pieceAt(cur);
      if (pieceOpt.isPresent()) {
        if (pieceOpt.get().isColor(piece.color())) {
          return false;
        }
        if (foundOpponent) {
          return false;
        }
        foundOpponent = true;
      }
    }
    return true;
  }
}
