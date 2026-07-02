package edu.austral.dissis.chess.game;

import edu.austral.dissis.chess.model.ChessExtra;
import edu.austral.dissis.chess.model.ChessMove;
import edu.austral.dissis.chess.model.ChessMoveType;
import edu.austral.dissis.chess.model.PieceType;
import edu.austral.dissis.chess.rules.BoardUpdater;
import edu.austral.dissis.chess.rules.NextStateBuilder;
import edu.austral.dissis.common.game.Game;
import edu.austral.dissis.common.model.Board;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.MoveResult;
import edu.austral.dissis.common.rules.MoveValidator;
import edu.austral.dissis.common.rules.TurnManager;
import edu.austral.dissis.common.rules.WinCondition;

public class ChessGame extends Game {

  private final BoardUpdater boardUpdater;
  private final NextStateBuilder nextStateBuilder;

  public ChessGame(
      GameState initialState,
      MoveValidator moveValidator,
      WinCondition winCondition,
      TurnManager turnManager,
      BoardUpdater boardUpdater,
      NextStateBuilder nextStateBuilder) {
    super(initialState, moveValidator, winCondition, turnManager);
    this.boardUpdater = boardUpdater;
    this.nextStateBuilder = nextStateBuilder;
  }

  @Override
  public MoveResult executeMove(Move move) {
    if (currentState().isOver()) {
      return new MoveResult.Failure("El juego ya terminó");
    }
    Move normalizedMove = normalizeMove(move);
    var violation = moveValidator.findViolation(normalizedMove, currentState());
    if (violation.isPresent()) {
      return new MoveResult.Failure(violation.get());
    }
    Board newBoard = boardUpdater.apply(normalizedMove, currentState());
    GameState newState =
        nextStateBuilder.build(normalizedMove, currentState(), newBoard, turnManager, winCondition);
    commitState(newState);
    return new MoveResult.Success(newState);
  }

  private Move normalizeMove(Move move) {
    if (move instanceof ChessMove cm && cm.type() != ChessMoveType.STANDARD) {
      return move;
    }
    return currentState()
        .board()
        .pieceAt(move.from())
        .map(
            p -> {
              if (p.isType(PieceType.KING)) {
                return detectCastling(move, p);
              }
              if (p.isType(PieceType.PAWN)) {
                return detectEnPassant(move);
              }
              return move;
            })
        .orElse(move);
  }

  private Move detectCastling(Move move, edu.austral.dissis.common.model.Piece piece) {
    int expectedRow = piece.color() == edu.austral.dissis.common.model.Color.WHITE ? 0 : 7;
    if (move.from().row() != expectedRow || move.from().col() != 4) {
      return move;
    }
    if (move.to().row() != move.from().row()) {
      return move;
    }
    int dc = move.to().col() - move.from().col();
    if (dc == 2) {
      return ChessMove.castlingKingside(move.from(), move.to());
    }
    if (dc == -2) {
      return ChessMove.castlingQueenside(move.from(), move.to());
    }
    return move;
  }

  private Move detectEnPassant(Move move) {
    int dr = move.to().row() - move.from().row();
    int dc = Math.abs(move.to().col() - move.from().col());
    if (Math.abs(dr) != 1 || dc != 1 || currentState().board().pieceAt(move.to()).isPresent()) {
      return move;
    }
    if (!(currentState().extra() instanceof ChessExtra extra)) {
      return move;
    }
    if (extra.enPassantTarget().map(move.to()::equals).orElse(false)) {
      return ChessMove.enPassant(move.from(), move.to());
    }
    return move;
  }
}
