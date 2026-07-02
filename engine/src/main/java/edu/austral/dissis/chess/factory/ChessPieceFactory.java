package edu.austral.dissis.chess.factory;

import edu.austral.dissis.chess.model.PieceType;
import edu.austral.dissis.chess.strategy.BishopMoveStrategy;
import edu.austral.dissis.chess.strategy.KingMoveStrategy;
import edu.austral.dissis.chess.strategy.KnightMoveStrategy;
import edu.austral.dissis.chess.strategy.PawnMoveStrategy;
import edu.austral.dissis.chess.strategy.QueenMoveStrategy;
import edu.austral.dissis.chess.strategy.RookMoveStrategy;
import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.Piece;
import edu.austral.dissis.common.rules.MoveStrategy;

public class ChessPieceFactory {

  public static Piece create(Color color, PieceType type) {
    return new Piece(color, type, strategyFor(type));
  }

  private static MoveStrategy strategyFor(PieceType type) {
    return switch (type) {
      case KING -> new KingMoveStrategy();
      case QUEEN -> new QueenMoveStrategy();
      case ROOK -> new RookMoveStrategy();
      case BISHOP -> new BishopMoveStrategy();
      case KNIGHT -> new KnightMoveStrategy();
      case PAWN -> new PawnMoveStrategy();
    };
  }
}
