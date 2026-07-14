package edu.austral.dissis.common.model;

public record GameState(Board board, Color currentPlayer, GameStatus status, GameExtra extra) {

  public boolean isOver() {
    return status != GameStatus.IN_PROGRESS;
  }
}
