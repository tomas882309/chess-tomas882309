package edu.austral.dissis.common.model;

public record Position(int row, int col) {

  public Position offset(int deltaRow, int deltaCol) {
    return new Position(row + deltaRow, col + deltaCol);
  }

  public boolean isWithinBounds(int size) {
    return row >= 0 && col >= 0 && row < size && col < size;
  }
}
