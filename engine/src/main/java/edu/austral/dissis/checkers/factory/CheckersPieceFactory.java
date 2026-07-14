package edu.austral.dissis.checkers.factory;

import edu.austral.dissis.checkers.model.pieces.CheckersKing;
import edu.austral.dissis.checkers.model.pieces.CheckersMan;
import edu.austral.dissis.checkers.strategy.CheckersKingMoveStrategy;
import edu.austral.dissis.checkers.strategy.CheckersManMoveStrategy;
import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.Piece;

public class CheckersPieceFactory {

  public static Piece man(Color color) {
    return new Piece(color, CheckersMan.INSTANCE, new CheckersManMoveStrategy());
  }

  public static Piece king(Color color) {
    return new Piece(color, CheckersKing.INSTANCE, new CheckersKingMoveStrategy());
  }
}
