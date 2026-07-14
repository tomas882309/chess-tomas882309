package edu.austral.dissis.common.model;

import edu.austral.dissis.common.rules.MoveStrategy;

public record Piece(Color color, PieceKind type, MoveStrategy moveStrategy) {

  public boolean isColor(Color expected) {
    return color == expected;
  }

  public boolean isType(PieceKind expected) {
    return type.equals(expected);
  }
}