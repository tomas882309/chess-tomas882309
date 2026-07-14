package edu.austral.dissis.checkers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.austral.dissis.checkers.factory.CheckersGameFactory;
import edu.austral.dissis.checkers.factory.CheckersPieceFactory;
import edu.austral.dissis.checkers.game.CheckersGameEngine;
import edu.austral.dissis.checkers.model.CheckersExtra;
import edu.austral.dissis.checkers.model.CheckersMove;
import edu.austral.dissis.checkers.model.pieces.CheckersKing;
import edu.austral.dissis.checkers.rules.CheckersBoardUpdater;
import edu.austral.dissis.checkers.rules.CheckersMoveValidator;
import edu.austral.dissis.checkers.rules.CheckersWinCondition;
import edu.austral.dissis.common.game.Game;
import edu.austral.dissis.common.model.Board;
import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.GameStatus;
import edu.austral.dissis.common.model.MoveResult;
import edu.austral.dissis.common.model.Piece;
import edu.austral.dissis.common.model.Position;
import edu.austral.dissis.common.rules.StandardTurnManager;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CheckersGameEngineTest {

  private Game buildGame(Map<Position, Piece> pieces, Color currentPlayer) {
    Board board = new Board(pieces, 8);
    GameState state =
        new GameState(board, currentPlayer, GameStatus.IN_PROGRESS, CheckersExtra.none());
    CheckersMoveValidator validator = new CheckersMoveValidator();
    CheckersGameEngine engine =
        new CheckersGameEngine(
            validator,
            new CheckersWinCondition(),
            new StandardTurnManager(),
            new CheckersBoardUpdater());
    return new Game(engine, state);
  }

  private Game buildGame(Map<Position, Piece> pieces) {
    return buildGame(pieces, Color.WHITE);
  }

  private Piece man(Color color) {
    return CheckersPieceFactory.man(color);
  }

  private Piece king(Color color) {
    return CheckersPieceFactory.king(color);
  }

  @Test
  void whiteMenCanMoveDiagonallyForward() {
    Game game = CheckersGameFactory.createStandardGame();
    MoveResult result = game.executeMove(new CheckersMove(new Position(2, 1), new Position(3, 0)));
    assertInstanceOf(MoveResult.Success.class, result);
  }

  @Test
  void blackCannotMoveOnWhitesTurn() {
    Game game = CheckersGameFactory.createStandardGame();
    MoveResult result = game.executeMove(new CheckersMove(new Position(5, 0), new Position(4, 1)));
    assertInstanceOf(MoveResult.Failure.class, result);
  }

  @Test
  void whiteMenCannotMoveBackward() {
    Game game =
        buildGame(
            Map.of(
                new Position(3, 2), man(Color.WHITE),
                new Position(7, 7), man(Color.BLACK)));
    MoveResult result = game.executeMove(new CheckersMove(new Position(3, 2), new Position(2, 1)));
    assertInstanceOf(MoveResult.Failure.class, result);
  }

  @Test
  void whiteCanCaptureBlackPiece() {
    Game game =
        buildGame(
            Map.of(
                new Position(2, 2), man(Color.WHITE),
                new Position(3, 3), man(Color.BLACK),
                new Position(7, 7), man(Color.BLACK)));
    MoveResult result = game.executeMove(new CheckersMove(new Position(2, 2), new Position(4, 4)));
    assertInstanceOf(MoveResult.Success.class, result);
  }

  @Test
  void capturedPieceIsRemovedFromBoard() {
    Game game =
        buildGame(
            Map.of(
                new Position(2, 2), man(Color.WHITE),
                new Position(3, 3), man(Color.BLACK),
                new Position(7, 7), man(Color.BLACK)));
    MoveResult.Success result =
        (MoveResult.Success)
            game.executeMove(new CheckersMove(new Position(2, 2), new Position(4, 4)));
    assertTrue(result.newState().board().pieceAt(new Position(3, 3)).isEmpty());
  }

  @Test
  void simpleMoveInvalidWhenCaptureAvailable() {
    Game game =
        buildGame(
            Map.of(
                new Position(2, 2), man(Color.WHITE),
                new Position(3, 3), man(Color.BLACK),
                new Position(7, 7), man(Color.BLACK)));
    MoveResult result = game.executeMove(new CheckersMove(new Position(2, 2), new Position(3, 1)));
    assertInstanceOf(MoveResult.Failure.class, result);
  }

  @Test
  void manPromotesToKingOnReachingLastRow() {
    Game game =
        buildGame(
            Map.of(
                new Position(6, 2), man(Color.WHITE),
                new Position(7, 7), man(Color.BLACK)));
    MoveResult.Success result =
        (MoveResult.Success)
            game.executeMove(new CheckersMove(new Position(6, 2), new Position(7, 1)));
    Piece promoted = result.newState().board().pieceAt(new Position(7, 1)).get();
    assertTrue(promoted.isType(CheckersKing.INSTANCE));
  }

  @Test
  void kingCanMoveBackward() {
    Game game =
        buildGame(
            Map.of(
                new Position(4, 4), king(Color.WHITE),
                new Position(7, 7), man(Color.BLACK)));
    MoveResult result = game.executeMove(new CheckersMove(new Position(4, 4), new Position(3, 3)));
    assertInstanceOf(MoveResult.Success.class, result);
  }

  @Test
  void kingCanCaptureBackward() {
    Game game =
        buildGame(
            Map.of(
                new Position(4, 4), king(Color.WHITE),
                new Position(3, 3), man(Color.BLACK),
                new Position(7, 7), man(Color.BLACK)));
    MoveResult result = game.executeMove(new CheckersMove(new Position(4, 4), new Position(2, 2)));
    assertInstanceOf(MoveResult.Success.class, result);
  }

  @Test
  void whiteWinsWhenLastBlackPieceIsCaptured() {
    Game game =
        buildGame(
            Map.of(
                new Position(2, 2), man(Color.WHITE),
                new Position(3, 3), man(Color.BLACK)));
    MoveResult.Success result =
        (MoveResult.Success)
            game.executeMove(new CheckersMove(new Position(2, 2), new Position(4, 4)));
    assertEquals(GameStatus.WHITE_WINS, result.newState().status());
  }

  @Test
  void multiJumpStaysOnSamePlayersTurn() {
    Game game =
        buildGame(
            Map.of(
                new Position(2, 0), man(Color.WHITE),
                new Position(3, 1), man(Color.BLACK),
                new Position(5, 1), man(Color.BLACK),
                new Position(7, 7), man(Color.BLACK)));
    MoveResult.Success firstJump =
        (MoveResult.Success)
            game.executeMove(new CheckersMove(new Position(2, 0), new Position(4, 2)));
    assertEquals(Color.WHITE, firstJump.newState().currentPlayer());
  }

  @Test
  void multiJumpSecondCaptureSucceeds() {
    Game game =
        buildGame(
            Map.of(
                new Position(2, 0), man(Color.WHITE),
                new Position(3, 1), man(Color.BLACK),
                new Position(5, 1), man(Color.BLACK),
                new Position(7, 7), man(Color.BLACK)));
    game.executeMove(new CheckersMove(new Position(2, 0), new Position(4, 2)));
    MoveResult result = game.executeMove(new CheckersMove(new Position(4, 2), new Position(6, 0)));
    assertInstanceOf(MoveResult.Success.class, result);
  }

  @Test
  void undoRestoresPreviousBoard() {
    Game game = CheckersGameFactory.createStandardGame();
    game.executeMove(new CheckersMove(new Position(2, 1), new Position(3, 0)));
    MoveResult.Success undo = (MoveResult.Success) game.undo();
    assertTrue(undo.newState().board().pieceAt(new Position(2, 1)).isPresent());
    assertTrue(undo.newState().board().pieceAt(new Position(3, 0)).isEmpty());
  }

  @Test
  void cannotMoveToOccupiedSquare() {
    Game game =
        buildGame(
            Map.of(
                new Position(2, 2), man(Color.WHITE),
                new Position(3, 3), man(Color.WHITE),
                new Position(7, 7), man(Color.BLACK)));
    MoveResult result = game.executeMove(new CheckersMove(new Position(2, 2), new Position(3, 3)));
    assertInstanceOf(MoveResult.Failure.class, result);
  }

  @Test
  void cannotMoveOutOfBounds() {
    Game game =
        buildGame(
            Map.of(
                new Position(0, 0), man(Color.WHITE),
                new Position(7, 7), man(Color.BLACK)));
    MoveResult result =
        game.executeMove(new CheckersMove(new Position(0, 0), new Position(-1, -1)));
    assertInstanceOf(MoveResult.Failure.class, result);
  }
}
