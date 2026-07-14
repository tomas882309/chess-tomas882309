package edu.austral.dissis.chess.strategy;

import edu.austral.dissis.common.model.Board;
import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Piece;
import edu.austral.dissis.common.rules.MoveStrategy;

public class BerolinaPawnMoveStrategy implements MoveStrategy {

  @Override
  public boolean isValidMove(Move move, Piece piece, GameState state) {
    int direction = piece.isColor(Color.WHITE) ? 1 : -1;
    return isDiagonalForward(move, direction, state.board())
        || isDoubleForward(move, direction, piece.color(), state.board())
        || isStraightCapture(move, direction, state.board());
  }

  private boolean isDiagonalForward(Move move, int direction, Board board) {
    int dr = move.to().row() - move.from().row();
    int dc = Math.abs(move.to().col() - move.from().col());
    return dr == direction && dc == 1 && board.pieceAt(move.to()).isEmpty();
  }

  private boolean isDoubleForward(Move move, int direction, Color color, Board board) {
    int dr = move.to().row() - move.from().row();
    int dc = Math.abs(move.to().col() - move.from().col());
    boolean onStartRow = color == Color.WHITE ? move.from().row() == 1 : move.from().row() == 6;
    return dr == 2 * direction && dc == 2 && onStartRow && board.pieceAt(move.to()).isEmpty();
  }

  private boolean isStraightCapture(Move move, int direction, Board board) {
    int dr = move.to().row() - move.from().row();
    int dc = move.to().col() - move.from().col();
    return dr == direction && dc == 0 && board.pieceAt(move.to()).isPresent();
  }
}
