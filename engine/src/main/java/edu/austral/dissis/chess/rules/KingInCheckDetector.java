package edu.austral.dissis.chess.rules;

import edu.austral.dissis.chess.model.ChessMove;
import edu.austral.dissis.common.model.Board;
import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Piece;
import edu.austral.dissis.common.model.Position;
import java.util.Map;

public class KingInCheckDetector {

  public boolean isKingInCheck(Color color, GameState state) {
    Position kingPos = findKingPosition(color, state.board());
    return isSquareThreatened(kingPos, color, state);
  }

  public boolean wouldLeaveKingInCheck(Move move, Piece piece, GameState state) {
    Board newBoard = state.board().withMove(move.from(), move.to());
    GameState stateAfter =
        new GameState(newBoard, state.currentPlayer(), state.status(), state.extra());
    return isKingInCheck(piece.color(), stateAfter);
  }

  public boolean isSquareThreatened(Position pos, Color allyColor, GameState state) {
    Board boardForCheck = occupyIfEmpty(pos, allyColor, state.board());
    GameState stateForCheck =
        new GameState(boardForCheck, state.currentPlayer(), state.status(), state.extra());
    return boardForCheck.pieces().entrySet().stream()
        .filter(e -> !e.getValue().isColor(allyColor))
        .anyMatch(
            e ->
                e.getValue()
                    .moveStrategy()
                    .isValidMove(ChessMove.standard(e.getKey(), pos), e.getValue(), stateForCheck));
  }

  private Board occupyIfEmpty(Position pos, Color allyColor, Board board) {
    if (board.pieceAt(pos).isPresent()) {
      return board;
    }
    Piece dummy =
        new Piece(allyColor, edu.austral.dissis.chess.model.PieceType.PAWN, (m, p, s) -> false);
    return board.withPieceAt(pos, dummy);
  }

  private Position findKingPosition(Color color, Board board) {
    return board.pieces().entrySet().stream()
        .filter(
            e ->
                e.getValue().isColor(color)
                    && e.getValue().isType(edu.austral.dissis.chess.model.PieceType.KING))
        .map(Map.Entry::getKey)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("King not found for " + color));
  }
}
