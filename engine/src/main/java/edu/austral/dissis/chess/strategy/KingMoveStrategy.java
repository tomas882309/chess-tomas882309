package edu.austral.dissis.chess.strategy;

import edu.austral.dissis.chess.model.ChessMove;
import edu.austral.dissis.chess.rules.CastlingValidator;
import edu.austral.dissis.common.model.Board;
import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Piece;
import edu.austral.dissis.common.rules.MoveStrategy;

public class KingMoveStrategy implements MoveStrategy {

  private final CastlingValidator castlingValidator = new CastlingValidator();

  @Override
  public boolean isValidMove(Move move, Piece piece, GameState state) {
    if (move instanceof ChessMove cm) {
      return switch (cm.type()) {
        case CASTLING_KINGSIDE -> castlingValidator.canCastleKingside(piece.color(), state);
        case CASTLING_QUEENSIDE -> castlingValidator.canCastleQueenside(piece.color(), state);
        default -> isStandardKingMove(move, piece.color(), state.board());
      };
    }
    return isStandardKingMove(move, piece.color(), state.board());
  }

  private boolean isStandardKingMove(Move move, Color color, Board board) {
    int dr = Math.abs(move.to().row() - move.from().row());
    int dc = Math.abs(move.to().col() - move.from().col());
    return dr <= 1 && dc <= 1 && (dr + dc > 0) && !board.isOccupiedByColor(move.to(), color);
  }
}
