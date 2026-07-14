package edu.austral.dissis.chess.model.pieces;

import edu.austral.dissis.common.model.PieceKind;

public final class Queen implements PieceKind {
  public static final Queen INSTANCE = new Queen();

  private Queen() {}

  @Override
  public String pieceId() {
    return "queen";
  }
}
