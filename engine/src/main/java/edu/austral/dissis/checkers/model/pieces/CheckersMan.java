package edu.austral.dissis.checkers.model.pieces;

import edu.austral.dissis.common.model.PieceKind;

public final class CheckersMan implements PieceKind {
  public static final CheckersMan INSTANCE = new CheckersMan();

  private CheckersMan() {}

  @Override
  public String pieceId() {
    return "man";
  }
}
