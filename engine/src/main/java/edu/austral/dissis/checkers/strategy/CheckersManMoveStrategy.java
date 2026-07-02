package edu.austral.dissis.checkers.strategy;

import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Piece;
import edu.austral.dissis.common.model.Position;
import edu.austral.dissis.common.rules.MoveStrategy;

public class CheckersManMoveStrategy implements MoveStrategy {

  @Override
  public boolean isValidMove(Move move, Piece piece, GameState state) {
    int direction = piece.color() == Color.WHITE ? 1 : -1;
    int dr = move.to().row() - move.from().row();
    int dc = move.to().col() - move.from().col();

    if (!state.board().isWithinBounds(move.to())) {
      return false;
    }
    if (state.board().pieceAt(move.to()).isPresent()) {
      return false;
    }
    if (dr == direction && Math.abs(dc) == 1) {
      return true;
    }
    if (dr == 2 * direction && Math.abs(dc) == 2) {
      Position mid =
          new Position(
              (move.from().row() + move.to().row()) / 2, (move.from().col() + move.to().col()) / 2);
      return state.board().pieceAt(mid).map(p -> !p.isColor(piece.color())).orElse(false);
    }
    return false;
  }
}
