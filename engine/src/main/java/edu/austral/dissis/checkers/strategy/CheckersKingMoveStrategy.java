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
    if (Math.abs(dr) != Math.abs(dc) || dr == 0) return false;
    if (!state.board().isWithinBounds(move.to())) return false;
    if (state.board().pieceAt(move.to()).isPresent()) return false;
    return isPathValid(move, piece, state, Integer.signum(dr), Integer.signum(dc));
  }

  private boolean isPathValid(Move move, Piece piece, GameState state, int stepR, int stepC) {
    int dist = Math.abs(move.to().row() - move.from().row());
    boolean foundOpponent = false;
    for (int i = 1; i < dist; i++) {
      Position cur = move.from().offset(i * stepR, i * stepC);
      var result = evaluateSquare(cur, piece, state, foundOpponent);
      if (result == SquareResult.BLOCKED) return false;
      if (result == SquareResult.OPPONENT) foundOpponent = true;
    }
    return true;
  }

  private SquareResult evaluateSquare(Position pos, Piece piece, GameState state, boolean alreadyFoundOpponent) {
    return state.board().pieceAt(pos).map(p -> {
      if (p.isColor(piece.color())) return SquareResult.BLOCKED;
      if (alreadyFoundOpponent) return SquareResult.BLOCKED;
      return SquareResult.OPPONENT;
    }).orElse(SquareResult.EMPTY);
  }

  private enum SquareResult { EMPTY, OPPONENT, BLOCKED }
}