package edu.austral.dissis.common.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record Board(Map<Position, Piece> pieces, int size) {

  public Board {
    pieces = Map.copyOf(pieces);
  }

  public Optional<Piece> pieceAt(Position pos) {
    return Optional.ofNullable(pieces.get(pos));
  }

  public boolean isWithinBounds(Position pos) {
    return pos.isWithinBounds(size);
  }

  public boolean isOccupiedByColor(Position pos, Color color) {
    return pieceAt(pos).map(p -> p.isColor(color)).orElse(false);
  }

  public Board withMove(Position from, Position to) {
    var updated = new HashMap<>(pieces);
    updated.put(to, updated.remove(from));
    return new Board(updated, size);
  }

  public Board withPieceAt(Position pos, Piece piece) {
    var updated = new HashMap<>(pieces);
    updated.put(pos, piece);
    return new Board(updated, size);
  }

  public Board withoutPieceAt(Position pos) {
    var updated = new HashMap<>(pieces);
    updated.remove(pos);
    return new Board(updated, size);
  }
}
