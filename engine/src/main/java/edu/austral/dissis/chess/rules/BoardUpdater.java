package edu.austral.dissis.chess.rules;

import edu.austral.dissis.chess.factory.ChessPieceFactory;
import edu.austral.dissis.chess.model.ChessMove;
import edu.austral.dissis.common.model.Board;
import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Position;

public class BoardUpdater {

  public Board apply(Move move, GameState state) {
    if (!(move instanceof ChessMove cm)) {
      return state.board().withMove(move.from(), move.to());
    }
    return switch (cm.type()) {
      case STANDARD -> state.board().withMove(cm.from(), cm.to());
      case CASTLING_KINGSIDE -> applyCastlingKingside(cm, state.board());
      case CASTLING_QUEENSIDE -> applyCastlingQueenside(cm, state.board());
      case EN_PASSANT -> applyEnPassant(cm, state.board(), state.currentPlayer());
      case PROMOTION -> applyPromotion(cm, state.board());
    };
  }

  private Board applyCastlingKingside(ChessMove move, Board board) {
    int row = move.from().row();
    Board afterKing = board.withMove(move.from(), new Position(row, 6));
    return afterKing.withMove(new Position(row, 7), new Position(row, 5));
  }

  private Board applyCastlingQueenside(ChessMove move, Board board) {
    int row = move.from().row();
    Board afterKing = board.withMove(move.from(), new Position(row, 2));
    return afterKing.withMove(new Position(row, 0), new Position(row, 3));
  }

  private Board applyEnPassant(ChessMove move, Board board, Color currentPlayer) {
    int capturedRow = currentPlayer == Color.WHITE ? move.to().row() - 1 : move.to().row() + 1;
    return board
        .withMove(move.from(), move.to())
        .withoutPieceAt(new Position(capturedRow, move.to().col()));
  }

  private Board applyPromotion(ChessMove move, Board board) {
    Color color = board.pieceAt(move.from()).orElseThrow().color();
    return board.withoutPieceAt(move.from()).withPieceAt(move.to(), ChessPieceFactory.queen(color));
  }
}
