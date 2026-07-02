package edu.austral.dissis.chess.strategy;

import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Piece;
import edu.austral.dissis.common.rules.MoveStrategy;

public class QueenMoveStrategy implements MoveStrategy {

  private final RookMoveStrategy rook = new RookMoveStrategy();
  private final BishopMoveStrategy bishop = new BishopMoveStrategy();

  @Override
  public boolean isValidMove(Move move, Piece piece, GameState state) {
    return rook.isValidMove(move, piece, state) || bishop.isValidMove(move, piece, state);
  }
}
