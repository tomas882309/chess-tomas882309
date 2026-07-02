package edu.austral.dissis.chess.rules;

import edu.austral.dissis.chess.model.ChessExtra;
import edu.austral.dissis.common.model.Board;
import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Position;

public class CastlingValidator {

  private final KingInCheckDetector checkDetector = new KingInCheckDetector();

  public boolean canCastleKingside(Color color, GameState state) {
    if (!(state.extra() instanceof ChessExtra extra)) {
      return false;
    }
    if (!extra.castlingRights().canCastleKingside(color)) {
      return false;
    }
    int row = baseRowFor(color);
    return areColumnsClear(state.board(), row, 5, 6)
        && !isKingOrPathThreatened(color, row, new int[] {4, 5, 6}, state);
  }

  public boolean canCastleQueenside(Color color, GameState state) {
    if (!(state.extra() instanceof ChessExtra extra)) {
      return false;
    }
    if (!extra.castlingRights().canCastleQueenside(color)) {
      return false;
    }
    int row = baseRowFor(color);
    return areColumnsClear(state.board(), row, 1, 3)
        && !isKingOrPathThreatened(color, row, new int[] {4, 3, 2}, state);
  }

  private boolean areColumnsClear(Board board, int row, int fromCol, int toCol) {
    for (int col = fromCol; col <= toCol; col++) {
      if (board.pieceAt(new Position(row, col)).isPresent()) {
        return false;
      }
    }
    return true;
  }

  private boolean isKingOrPathThreatened(Color color, int row, int[] cols, GameState state) {
    for (int col : cols) {
      if (checkDetector.isSquareThreatened(new Position(row, col), color, state)) {
        return true;
      }
    }
    return false;
  }

  private int baseRowFor(Color color) {
    return color == Color.WHITE ? 0 : 7;
  }
}
