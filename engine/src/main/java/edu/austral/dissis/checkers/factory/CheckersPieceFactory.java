package edu.austral.dissis.checkers.factory;

import edu.austral.dissis.checkers.model.CheckersPieceType;
import edu.austral.dissis.checkers.strategy.CheckersKingMoveStrategy;
import edu.austral.dissis.checkers.strategy.CheckersManMoveStrategy;
import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.Piece;
import edu.austral.dissis.common.rules.MoveStrategy;

public class CheckersPieceFactory {

  public static Piece create(Color color, CheckersPieceType type) {
    return new Piece(color, type, strategyFor(type));
  }

  private static MoveStrategy strategyFor(CheckersPieceType type) {
    return switch (type) {
      case MAN -> new CheckersManMoveStrategy();
      case KING -> new CheckersKingMoveStrategy();
    };
  }
}
