package edu.austral.dissis.chess.factory;

import edu.austral.dissis.chess.game.ChessGameEngine;
import edu.austral.dissis.chess.model.CastlingRights;
import edu.austral.dissis.chess.model.ChessExtra;
import edu.austral.dissis.chess.model.PieceType;
import edu.austral.dissis.chess.rules.BoardUpdater;
import edu.austral.dissis.chess.rules.CheckmateWinCondition;
import edu.austral.dissis.chess.rules.ChessMoveValidator;
import edu.austral.dissis.chess.rules.NextStateBuilder;
import edu.austral.dissis.common.game.Game;
import edu.austral.dissis.common.model.Board;
import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.GameStatus;
import edu.austral.dissis.common.model.Piece;
import edu.austral.dissis.common.model.Position;
import edu.austral.dissis.common.rules.StandardTurnManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ChessGameFactory {

  private static final PieceType[] BACK_RANK = {
    PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
    PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
  };

  public static Game createStandardGame() {
    Board board = buildInitialBoard();
    GameState state =
        new GameState(board, Color.WHITE, GameStatus.IN_PROGRESS, ChessExtra.initial());
    return new Game(createEngine(), state);
  }

  public static Game createFromBoard(
      Board board, Color currentPlayer, CastlingRights castlingRights) {
    GameState state =
        new GameState(
            board,
            currentPlayer,
            GameStatus.IN_PROGRESS,
            new ChessExtra(castlingRights, Optional.empty()));
    return new Game(createEngine(), state);
  }

  private static ChessGameEngine createEngine() {
    return new ChessGameEngine(
        new ChessMoveValidator(),
        new CheckmateWinCondition(),
        new StandardTurnManager(),
        new BoardUpdater(),
        new NextStateBuilder());
  }

  private static Board buildInitialBoard() {
    Map<Position, Piece> pieces = new HashMap<>();
    placeBackRank(pieces, Color.WHITE, 0);
    placePawns(pieces, Color.WHITE, 1);
    placePawns(pieces, Color.BLACK, 6);
    placeBackRank(pieces, Color.BLACK, 7);
    return new Board(pieces, 8);
  }

  private static void placeBackRank(Map<Position, Piece> pieces, Color color, int row) {
    for (int col = 0; col < 8; col++) {
      pieces.put(new Position(row, col), ChessPieceFactory.create(color, BACK_RANK[col]));
    }
  }

  private static void placePawns(Map<Position, Piece> pieces, Color color, int row) {
    for (int col = 0; col < 8; col++) {
      pieces.put(new Position(row, col), ChessPieceFactory.create(color, PieceType.PAWN));
    }
  }
}
