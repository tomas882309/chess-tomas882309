package edu.austral.dissis.chess.factory;

import edu.austral.dissis.chess.game.ChessGameEngine;
import edu.austral.dissis.chess.model.CastlingRights;
import edu.austral.dissis.chess.model.ChessExtra;
import edu.austral.dissis.chess.model.pieces.Bishop;
import edu.austral.dissis.chess.model.pieces.King;
import edu.austral.dissis.chess.model.pieces.Knight;
import edu.austral.dissis.chess.model.pieces.Pawn;
import edu.austral.dissis.chess.model.pieces.Queen;
import edu.austral.dissis.chess.model.pieces.Rook;
import edu.austral.dissis.chess.rules.BoardUpdater;
import edu.austral.dissis.chess.rules.CheckmateWinCondition;
import edu.austral.dissis.chess.rules.ChessMoveValidator;
import edu.austral.dissis.chess.rules.ExtinctionWinCondition;
import edu.austral.dissis.chess.rules.NextStateBuilder;
import edu.austral.dissis.chess.rules.ProgressiveTurnManager;
import edu.austral.dissis.chess.strategy.BerolinaPawnMoveStrategy;
import edu.austral.dissis.common.game.Game;
import edu.austral.dissis.common.model.Board;
import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.GameStatus;
import edu.austral.dissis.common.model.Piece;
import edu.austral.dissis.common.model.PieceKind;
import edu.austral.dissis.common.model.Position;
import edu.austral.dissis.common.rules.StandardTurnManager;
import edu.austral.dissis.common.rules.TurnManager;
import edu.austral.dissis.common.rules.WinCondition;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ChessGameFactory {

  public static Game createStandardGame() {
    return buildGame(buildInitialBoard(), new StandardTurnManager(), new CheckmateWinCondition());
  }

  public static Game createExtinctionGame() {
    Set<PieceKind> required =
        Set.of(
            King.INSTANCE,
            Queen.INSTANCE,
            Rook.INSTANCE,
            Bishop.INSTANCE,
            Knight.INSTANCE,
            Pawn.INSTANCE);
    return buildGame(
        buildInitialBoard(), new StandardTurnManager(), new ExtinctionWinCondition(required));
  }

  public static Game createProgressiveGame() {
    return buildGame(
        buildInitialBoard(), new ProgressiveTurnManager(), new CheckmateWinCondition());
  }

  public static Game createBerlineseGame() {
    return buildGame(buildBerlineseBoard(), new StandardTurnManager(), new CheckmateWinCondition());
  }

  public static Game createFromBoard(
      Board board, Color currentPlayer, CastlingRights castlingRights) {
    GameState state =
        new GameState(
            board,
            currentPlayer,
            GameStatus.IN_PROGRESS,
            new ChessExtra(castlingRights, Optional.empty()));
    return new Game(createEngine(new StandardTurnManager(), new CheckmateWinCondition()), state);
  }

  private static Game buildGame(Board board, TurnManager turnManager, WinCondition winCondition) {
    GameState state =
        new GameState(board, Color.WHITE, GameStatus.IN_PROGRESS, ChessExtra.initial());
    return new Game(createEngine(turnManager, winCondition), state);
  }

  private static ChessGameEngine createEngine(TurnManager turnManager, WinCondition winCondition) {
    return new ChessGameEngine(
        new ChessMoveValidator(),
        winCondition,
        turnManager,
        new BoardUpdater(),
        new NextStateBuilder());
  }

  private static Board buildInitialBoard() {
    Map<Position, Piece> pieces = new HashMap<>();
    placeBackRank(pieces, Color.WHITE, 0);
    placePawns(pieces, Color.WHITE, 1, false);
    placePawns(pieces, Color.BLACK, 6, false);
    placeBackRank(pieces, Color.BLACK, 7);
    return new Board(pieces, 8);
  }

  private static Board buildBerlineseBoard() {
    Map<Position, Piece> pieces = new HashMap<>();
    placeBackRank(pieces, Color.WHITE, 0);
    placePawns(pieces, Color.WHITE, 1, true);
    placePawns(pieces, Color.BLACK, 6, true);
    placeBackRank(pieces, Color.BLACK, 7);
    return new Board(pieces, 8);
  }

  private static void placeBackRank(Map<Position, Piece> pieces, Color color, int row) {
    pieces.put(new Position(row, 0), ChessPieceFactory.rook(color));
    pieces.put(new Position(row, 1), ChessPieceFactory.knight(color));
    pieces.put(new Position(row, 2), ChessPieceFactory.bishop(color));
    pieces.put(new Position(row, 3), ChessPieceFactory.queen(color));
    pieces.put(new Position(row, 4), ChessPieceFactory.king(color));
    pieces.put(new Position(row, 5), ChessPieceFactory.bishop(color));
    pieces.put(new Position(row, 6), ChessPieceFactory.knight(color));
    pieces.put(new Position(row, 7), ChessPieceFactory.rook(color));
  }

  private static void placePawns(
      Map<Position, Piece> pieces, Color color, int row, boolean berolina) {
    for (int col = 0; col < 8; col++) {
      Piece pawn =
          berolina
              ? new Piece(color, Pawn.INSTANCE, new BerolinaPawnMoveStrategy())
              : ChessPieceFactory.pawn(color);
      pieces.put(new Position(row, col), pawn);
    }
  }
}
