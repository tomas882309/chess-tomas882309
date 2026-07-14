package edu.austral.dissis.common.network.payload;

import java.util.List;

public record GameStatePayload(
    List<Piece> pieces, String currentPlayer, String status, int boardSize) {

  public record Piece(String pieceId, String color, int row, int col) {}
}
