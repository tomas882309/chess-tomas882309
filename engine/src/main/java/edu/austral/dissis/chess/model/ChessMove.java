package edu.austral.dissis.chess.model;

import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Position;
import java.util.Optional;

public record ChessMove(
    Position from, Position to, ChessMoveType type, Optional<PieceType> promotionPiece)
    implements Move {

  public static ChessMove standard(Position from, Position to) {
    return new ChessMove(from, to, ChessMoveType.STANDARD, Optional.empty());
  }

  public static ChessMove castlingKingside(Position from, Position to) {
    return new ChessMove(from, to, ChessMoveType.CASTLING_KINGSIDE, Optional.empty());
  }

  public static ChessMove castlingQueenside(Position from, Position to) {
    return new ChessMove(from, to, ChessMoveType.CASTLING_QUEENSIDE, Optional.empty());
  }

  public static ChessMove enPassant(Position from, Position to) {
    return new ChessMove(from, to, ChessMoveType.EN_PASSANT, Optional.empty());
  }

  public static ChessMove promotion(Position from, Position to, PieceType piece) {
    return new ChessMove(from, to, ChessMoveType.PROMOTION, Optional.of(piece));
  }
}
