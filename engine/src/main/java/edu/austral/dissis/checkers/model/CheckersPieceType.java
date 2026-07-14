package edu.austral.dissis.checkers.model;

import edu.austral.dissis.common.model.PieceKind;

public enum CheckersPieceType implements PieceKind {
  MAN,
  KING;

  @Override
  public String pieceId() {
    return name().toLowerCase();
  }
}
