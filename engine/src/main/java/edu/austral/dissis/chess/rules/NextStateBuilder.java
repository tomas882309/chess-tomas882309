package edu.austral.dissis.chess.rules;

import edu.austral.dissis.chess.model.CastlingRights;
import edu.austral.dissis.chess.model.ChessExtra;
import edu.austral.dissis.chess.model.PieceType;
import edu.austral.dissis.common.model.Board;
import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.GameStatus;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Piece;
import edu.austral.dissis.common.model.Position;
import edu.austral.dissis.common.rules.TurnManager;
import edu.austral.dissis.common.rules.WinCondition;
import java.util.Optional;

public class NextStateBuilder {

  public GameState build(
      Move move,
      GameState current,
      Board newBoard,
      TurnManager turnManager,
      WinCondition winCondition) {
    Color nextPlayer = turnManager.nextPlayer(current.currentPlayer(), current);
    ChessExtra updatedExtra = updateExtra(move, current);
    GameState tentative = new GameState(newBoard, nextPlayer, GameStatus.IN_PROGRESS, updatedExtra);
    GameStatus status = winCondition.evaluate(tentative, move);
    return new GameState(newBoard, nextPlayer, status, updatedExtra);
  }

  private ChessExtra updateExtra(Move move, GameState state) {
    ChessExtra current = state.extra() instanceof ChessExtra e ? e : ChessExtra.initial();
    CastlingRights updatedRights = updateCastlingRights(move, state, current);
    Optional<Position> enPassantTarget = computeEnPassantTarget(move, state.board());
    return new ChessExtra(updatedRights, enPassantTarget);
  }

  private CastlingRights updateCastlingRights(Move move, GameState state, ChessExtra extra) {
    return state
        .board()
        .pieceAt(move.from())
        .map(p -> revokeBasedOnPiece(p, move, extra.castlingRights()))
        .orElse(extra.castlingRights());
  }

  private CastlingRights revokeBasedOnPiece(Piece piece, Move move, CastlingRights rights) {
    if (piece.isType(PieceType.KING)) {
      return rights.revokeAll(piece.color());
    }
    if (piece.isType(PieceType.ROOK)) {
      return revokeRookSide(piece.color(), move.from(), rights);
    }
    return rights;
  }

  private CastlingRights revokeRookSide(Color color, Position from, CastlingRights rights) {
    if (from.col() == 7) {
      return rights.revokeKingside(color);
    }
    if (from.col() == 0) {
      return rights.revokeQueenside(color);
    }
    return rights;
  }

  private Optional<Position> computeEnPassantTarget(Move move, Board board) {
    return board
        .pieceAt(move.from())
        .filter(p -> p.isType(PieceType.PAWN) && Math.abs(move.to().row() - move.from().row()) == 2)
        .map(p -> new Position((move.from().row() + move.to().row()) / 2, move.from().col()));
  }
}
