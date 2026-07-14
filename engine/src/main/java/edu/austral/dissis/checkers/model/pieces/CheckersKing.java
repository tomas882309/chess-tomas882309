package edu.austral.dissis.checkers.model.pieces;

import edu.austral.dissis.common.model.PieceKind;

public final class CheckersKing implements PieceKind {
  public static final CheckersKing INSTANCE = new CheckersKing();

  private CheckersKing() {}

  @Override
  public String pieceId() {
    return "king";
  }
}
