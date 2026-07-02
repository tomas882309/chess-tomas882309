package edu.austral.dissis.chess.strategy;

import edu.austral.dissis.chess.model.ChessExtra;
import edu.austral.dissis.common.model.Board;
import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Piece;
import edu.austral.dissis.common.model.Position;
import edu.austral.dissis.common.rules.MoveStrategy;

public class PawnMoveStrategy implements MoveStrategy {

  @Override
  public boolean isValidMove(Move move, Piece piece, GameState state) {
    int direction = piece.isColor(Color.WHITE) ? 1 : -1;
    return isStandardForward(move, direction, state.board())
        || isDoubleForward(move, direction, piece.color(), state.board())
        || isDiagonalCapture(move, direction, state.board())
        || isEnPassant(move, direction, state);
  }

  private boolean isStandardForward(Move move, int direction, Board board) {
    int dr = move.to().row() - move.from().row();
    int dc = move.to().col() - move.from().col();
    return dr == direction && dc == 0 && board.pieceAt(move.to()).isEmpty();
  }

  private boolean isDoubleForward(Move move, int direction, Color color, Board board) {
    int dr = move.to().row() - move.from().row();
    int dc = move.to().col() - move.from().col();
    if (dr != 2 * direction || dc != 0) {
      return false;
    }
    boolean onStartRow = color == Color.WHITE ? move.from().row() == 1 : move.from().row() == 6;
    Position intermediate = move.from().offset(direction, 0);
    return onStartRow
        && board.pieceAt(intermediate).isEmpty()
        && board.pieceAt(move.to()).isEmpty();
  }

  private boolean isDiagonalCapture(Move move, int direction, Board board) {
    int dr = move.to().row() - move.from().row();
    int dc = Math.abs(move.to().col() - move.from().col());
    return dr == direction && dc == 1 && board.pieceAt(move.to()).isPresent();
  }

  private boolean isEnPassant(Move move, int direction, GameState state) {
    int dr = move.to().row() - move.from().row();
    int dc = Math.abs(move.to().col() - move.from().col());
    if (dr != direction || dc != 1) {
      return false;
    }
    if (!(state.extra() instanceof ChessExtra extra)) {
      return false;
    }
    return extra.enPassantTarget().map(move.to()::equals).orElse(false);
  }
}
