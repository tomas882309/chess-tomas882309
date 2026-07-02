package edu.austral.dissis.common.model;

import edu.austral.dissis.common.rules.MoveStrategy;

public record Piece(Color color, Object type, MoveStrategy moveStrategy) {

  public boolean isColor(Color expected) {
    return color == expected;
  }

  public boolean isType(Object expected) {
    return type.equals(expected);
  }
}
