package edu.austral.dissis.checkers.model;

import edu.austral.dissis.common.model.GameExtra;
import edu.austral.dissis.common.model.Position;
import java.util.Optional;

public record CheckersExtra(Optional<Position> multiJumpPosition) implements GameExtra {

  public static CheckersExtra none() {
    return new CheckersExtra(Optional.empty());
  }

  public static CheckersExtra multiJump(Position pos) {
    return new CheckersExtra(Optional.of(pos));
  }

  public boolean isMultiJump() {
    return multiJumpPosition.isPresent();
  }
}
