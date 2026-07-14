package edu.austral.dissis.chess.game;

import edu.austral.dissis.chess.model.ChessExtra;
import edu.austral.dissis.chess.model.ChessMove;
import edu.austral.dissis.chess.model.ChessMoveType;
import edu.austral.dissis.chess.model.pieces.King;
import edu.austral.dissis.chess.model.pieces.Pawn;
import edu.austral.dissis.chess.rules.BoardUpdater;
import edu.austral.dissis.chess.rules.NextStateBuilder;
import edu.austral.dissis.common.game.GameEngine;
import edu.austral.dissis.common.model.Board;
import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.MoveResult;
import edu.austral.dissis.common.model.Piece;
import edu.austral.dissis.common.model.Position;
import edu.austral.dissis.common.rules.MoveValidator;
import edu.austral.dissis.common.rules.TurnManager;
import edu.austral.dissis.common.rules.WinCondition;

public class ChessGameEngine implements GameEngine {

  private final MoveValidator moveValidator;
  private final WinCondition winCondition;
  private final TurnManager turnManager;
  private final BoardUpdater boardUpdater;
  private final NextStateBuilder nextStateBuilder;

  public ChessGameEngine(
      MoveValidator moveValidator,
      WinCondition winCondition,
      TurnManager turnManager,
      BoardUpdater boardUpdater,
      NextStateBuilder nextStateBuilder) {
    this.moveValidator = moveValidator;
    this.winCondition = winCondition;
    this.turnManager = turnManager;
    this.boardUpdater = boardUpdater;
    this.nextStateBuilder = nextStateBuilder;
  }

  @Override
  public MoveResult executeMove(Move move, GameState state) {
    if (state.isOver()) {
      return new MoveResult.Failure("El juego ya terminó");
    }
    Move normalized = normalizeMove(move, state);
    var violation = moveValidator.findViolation(normalized, state);
    if (violation.isPresent()) {
      return new MoveResult.Failure(violation.get());
    }
    Board newBoard = boardUpdater.apply(normalized, state);
    GameState newState =
        nextStateBuilder.build(normalized, state, newBoard, turnManager, winCondition);
    return new MoveResult.Success(newState);
  }

  private Move normalizeMove(Move move, GameState state) {
    if (move instanceof ChessMove cm && cm.type() != ChessMoveType.STANDARD) {
      return move;
    }
    return state
        .board()
        .pieceAt(move.from())
        .map(p -> normalizeByPieceType(move, p, state))
        .orElse(move);
  }

  private Move normalizeByPieceType(Move move, Piece piece, GameState state) {
    if (piece.isType(King.INSTANCE)) {
      return detectCastling(move, piece);
    }
    if (piece.isType(Pawn.INSTANCE)) {
      return detectPawnMove(move, piece, state);
    }
    return move;
  }

  private Move detectPawnMove(Move move, Piece piece, GameState state) {
    Move withEnPassant = detectEnPassant(move, state);
    if (withEnPassant != move) {
      return withEnPassant;
    }
    return detectPromotion(move, piece);
  }

  private Move detectPromotion(Move move, Piece piece) {
    int promotionRow = piece.color() == Color.WHITE ? 7 : 0;
    if (move.to().row() == promotionRow) {
      return ChessMove.promotion(move.from(), move.to());
    }
    return move;
  }

  private Move detectCastling(Move move, Piece piece) {
    int expectedRow = piece.color() == Color.WHITE ? 0 : 7;
    if (!isKingOnStartSquare(move.from(), expectedRow)) {
      return move;
    }
    if (move.to().row() != move.from().row()) {
      return move;
    }
    return castlingMoveFor(move, move.to().col() - move.from().col());
  }

  private boolean isKingOnStartSquare(Position from, int expectedRow) {
    return from.row() == expectedRow && from.col() == 4;
  }

  private Move castlingMoveFor(Move move, int colDelta) {
    if (colDelta == 2) {
      return ChessMove.castlingKingside(move.from(), move.to());
    }
    if (colDelta == -2) {
      return ChessMove.castlingQueenside(move.from(), move.to());
    }
    return move;
  }

  private Move detectEnPassant(Move move, GameState state) {
    int dr = move.to().row() - move.from().row();
    int dc = Math.abs(move.to().col() - move.from().col());
    if (Math.abs(dr) != 1 || dc != 1 || state.board().pieceAt(move.to()).isPresent()) {
      return move;
    }
    if (!(state.extra() instanceof ChessExtra extra)) {
      return move;
    }
    if (extra.enPassantTarget().map(move.to()::equals).orElse(false)) {
      return ChessMove.enPassant(move.from(), move.to());
    }
    return move;
  }
}
