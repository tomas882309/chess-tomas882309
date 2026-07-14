package edu.austral.dissis.checkers.rules;

import edu.austral.dissis.checkers.factory.CheckersPieceFactory;
import edu.austral.dissis.checkers.model.pieces.CheckersMan;
import edu.austral.dissis.common.model.Board;
import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Position;

public class CheckersBoardUpdater {

  private final CheckersMoveValidator validator = new CheckersMoveValidator();

  public Board apply(Move move, Board board, Color currentPlayer) {
    Board afterMove = board.withMove(move.from(), move.to());
    if (validator.isCapture(move, board)) {
      afterMove = removeCaptured(move, afterMove);
    }
    afterMove = promoteIfNeeded(move.to(), afterMove, currentPlayer);
    return afterMove;
  }

  private Board removeCaptured(Move move, Board board) {
    int stepR = Integer.signum(move.to().row() - move.from().row());
    int stepC = Integer.signum(move.to().col() - move.from().col());
    int dist = Math.abs(move.to().row() - move.from().row());
    for (int i = 1; i < dist; i++) {
      Position cur = move.from().offset(i * stepR, i * stepC);
      if (board.pieceAt(cur).isPresent()) {
        return board.withoutPieceAt(cur);
      }
    }
    return board;
  }

  private Board promoteIfNeeded(Position to, Board board, Color currentPlayer) {
    int promotionRow = currentPlayer == Color.WHITE ? board.size() - 1 : 0;
    if (to.row() != promotionRow) {
      return board;
    }
    return board
        .pieceAt(to)
        .filter(p -> p.isType(CheckersMan.INSTANCE))
        .map(p -> board.withPieceAt(to, CheckersPieceFactory.king(p.color())))
        .orElse(board);
  }

  public boolean wasCapture(Move move, Board boardBefore) {
    return validator.isCapture(move, boardBefore);
  }
}
