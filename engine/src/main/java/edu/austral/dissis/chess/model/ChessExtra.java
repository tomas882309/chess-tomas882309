package edu.austral.dissis.chess.model;

import edu.austral.dissis.common.model.Position;
import java.util.Optional;

public record ChessExtra(CastlingRights castlingRights, Optional<Position> enPassantTarget) {

  public static ChessExtra initial() {
    return new ChessExtra(CastlingRights.allEnabled(), Optional.empty());
  }
}
