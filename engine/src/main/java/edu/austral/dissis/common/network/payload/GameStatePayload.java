package edu.austral.dissis.common.network.payload;

import java.util.List;

public class GameStatePayload {

  private List<Piece> pieces;
  private String currentPlayer;
  private String status;
  private int boardSize;

  public GameStatePayload() {}

  public GameStatePayload(List<Piece> pieces, String currentPlayer, String status, int boardSize) {
    this.pieces = pieces;
    this.currentPlayer = currentPlayer;
    this.status = status;
    this.boardSize = boardSize;
  }

  public List<Piece> getPieces() {
    return pieces;
  }

  public String getCurrentPlayer() {
    return currentPlayer;
  }

  public String getStatus() {
    return status;
  }

  public int getBoardSize() {
    return boardSize;
  }

  public void setPieces(List<Piece> pieces) {
    this.pieces = pieces;
  }

  public void setCurrentPlayer(String currentPlayer) {
    this.currentPlayer = currentPlayer;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setBoardSize(int boardSize) {
    this.boardSize = boardSize;
  }

  public static class Piece {

    private String pieceId;
    private String color;
    private int row;
    private int col;

    public Piece() {}

    public Piece(String pieceId, String color, int row, int col) {
      this.pieceId = pieceId;
      this.color = color;
      this.row = row;
      this.col = col;
    }

    public String getPieceId() {
      return pieceId;
    }

    public String getColor() {
      return color;
    }

    public int getRow() {
      return row;
    }

    public int getCol() {
      return col;
    }

    public void setPieceId(String pieceId) {
      this.pieceId = pieceId;
    }

    public void setColor(String color) {
      this.color = color;
    }

    public void setRow(int row) {
      this.row = row;
    }

    public void setCol(int col) {
      this.col = col;
    }
  }
}
