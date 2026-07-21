package edu.austral.dissis.checkers;

import edu.austral.dissis.checkers.factory.CheckersGameFactory;
import edu.austral.dissis.checkers.factory.CheckersPieceFactory;
import edu.austral.dissis.checkers.model.MultiCaptureExtraState;
import edu.austral.dissis.checkers.model.pieces.King;
import edu.austral.dissis.common.game.Game;
import edu.austral.dissis.common.game.GameResult;
import edu.austral.dissis.common.model.*;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CheckersGameEngineTest {

  private Game buildGame(Map<Position, Piece> pieces, Color currentPlayer) {
    Board board = new Board(pieces, 8, 8);
    return CheckersGameFactory.createFromBoard(board, currentPlayer);
  }

  private Game buildGame(Map<Position, Piece> pieces) {
    return buildGame(pieces, Color.WHITE);
  }

  @Test
  void whiteMenCanMoveDiagonallyForward() {
    Game game = CheckersGameFactory.createStandardGame();
    GameResult result = game.executeMove(new Move(new Position(5, 1), new Position(4, 2)));
    assertInstanceOf(GameResult.Moved.class, result);
  }

  @Test
  void blackCannotMoveOnWhitesTurn() {
    Game game = CheckersGameFactory.createStandardGame();
    GameResult result = game.executeMove(new Move(new Position(2, 0), new Position(3, 1)));
    assertInstanceOf(GameResult.Invalid.class, result);
  }

  @Test
  void whiteMenCannotMoveBackward() {
    Game game = buildGame(Map.of(
            new Position(4, 1), CheckersPieceFactory.man(Color.WHITE),
            new Position(7, 7), CheckersPieceFactory.man(Color.BLACK)));
    GameResult result = game.executeMove(new Move(new Position(4, 1), new Position(5, 0)));
    assertInstanceOf(GameResult.Invalid.class, result);
  }

  @Test
  void whiteCanCaptureBlackPiece() {
    Game game = buildGame(Map.of(
            new Position(4, 3), CheckersPieceFactory.man(Color.WHITE),
            new Position(3, 2), CheckersPieceFactory.man(Color.BLACK),
            new Position(7, 7), CheckersPieceFactory.man(Color.BLACK)));
    GameResult result = game.executeMove(new Move(new Position(4, 3), new Position(2, 1)));
    assertInstanceOf(GameResult.Moved.class, result);
  }

  @Test
  void capturedPieceIsRemovedFromBoard() {
    Game game = buildGame(Map.of(
            new Position(4, 3), CheckersPieceFactory.man(Color.WHITE),
            new Position(3, 2), CheckersPieceFactory.man(Color.BLACK),
            new Position(7, 7), CheckersPieceFactory.man(Color.BLACK)));
    GameResult.Moved result = (GameResult.Moved) game.executeMove(
            new Move(new Position(4, 3), new Position(2, 1)));
    assertTrue(result.newGame().current().board().pieceAt(new Position(3, 2)).isEmpty());
  }

  @Test
  void simpleMoveInvalidWhenCaptureAvailable() {
    Game game = buildGame(Map.of(
            new Position(4, 3), CheckersPieceFactory.man(Color.WHITE),
            new Position(3, 2), CheckersPieceFactory.man(Color.BLACK),
            new Position(7, 7), CheckersPieceFactory.man(Color.BLACK)));
    GameResult result = game.executeMove(new Move(new Position(4, 3), new Position(3, 4)));
    assertInstanceOf(GameResult.Invalid.class, result);
  }

  @Test
  void manPromotesToKingOnReachingLastRow() {
    Game game = buildGame(Map.of(
            new Position(1, 0), CheckersPieceFactory.man(Color.WHITE),
            new Position(7, 7), CheckersPieceFactory.man(Color.BLACK)));
    GameResult.Moved result = (GameResult.Moved) game.executeMove(
            new Move(new Position(1, 0), new Position(0, 1)));
    Piece promoted = result.newGame().current().board().pieceAt(new Position(0, 1)).get();
    assertTrue(promoted.isType(King.INSTANCE));
  }

  @Test
  void kingCanMoveBackward() {
    Game game = buildGame(Map.of(
            new Position(4, 4), CheckersPieceFactory.king(Color.WHITE),
            new Position(7, 7), CheckersPieceFactory.man(Color.BLACK)));
    GameResult result = game.executeMove(new Move(new Position(4, 4), new Position(5, 5)));
    assertInstanceOf(GameResult.Moved.class, result);
  }

  @Test
  void kingCanCaptureBackward() {
    Game game = buildGame(Map.of(
            new Position(4, 4), CheckersPieceFactory.king(Color.WHITE),
            new Position(5, 5), CheckersPieceFactory.man(Color.BLACK),
            new Position(0, 0), CheckersPieceFactory.man(Color.BLACK)));
    GameResult result = game.executeMove(new Move(new Position(4, 4), new Position(6, 6)));
    assertInstanceOf(GameResult.Moved.class, result);
  }

  @Test
  void whiteWinsWhenLastBlackPieceIsCaptured() {
    Game game = buildGame(Map.of(
            new Position(4, 3), CheckersPieceFactory.man(Color.WHITE),
            new Position(3, 2), CheckersPieceFactory.man(Color.BLACK)));
    GameResult.Moved result = (GameResult.Moved) game.executeMove(
            new Move(new Position(4, 3), new Position(2, 1)));
    assertEquals(GameStatus.WHITE_WINS, result.newGame().current().status());
  }

  @Test
  void multiJumpStaysOnSamePlayersTurn() {
    Game game = buildGame(Map.of(
            new Position(4, 3), CheckersPieceFactory.man(Color.WHITE),
            new Position(3, 2), CheckersPieceFactory.man(Color.BLACK),
            new Position(1, 2), CheckersPieceFactory.man(Color.BLACK)));
    GameResult.Moved result = (GameResult.Moved) game.executeMove(
            new Move(new Position(4, 3), new Position(2, 1)));
    assertEquals(Color.WHITE, result.newGame().current().currentPlayer());
  }

  @Test
  void multiJumpSecondCaptureSucceeds() {
    Game game = buildGame(Map.of(
            new Position(4, 3), CheckersPieceFactory.man(Color.WHITE),
            new Position(3, 2), CheckersPieceFactory.man(Color.BLACK),
            new Position(1, 2), CheckersPieceFactory.man(Color.BLACK)));
    game = ((GameResult.Moved) game.executeMove(
            new Move(new Position(4, 3), new Position(2, 1)))).newGame();
    GameResult result = game.executeMove(new Move(new Position(2, 1), new Position(0, 3)));
    assertInstanceOf(GameResult.Moved.class, result);
  }

  @Test
  void undoRestoresPreviousBoard() {
    Game game = CheckersGameFactory.createStandardGame();
    game = ((GameResult.Moved) game.executeMove(
            new Move(new Position(5, 1), new Position(4, 2)))).newGame();
    Game undone = game.undo();
    assertTrue(undone.current().board().pieceAt(new Position(5, 1)).isPresent());
    assertTrue(undone.current().board().pieceAt(new Position(4, 2)).isEmpty());
  }

  @Test
  void cannotMoveToOccupiedSquare() {
    Game game = CheckersGameFactory.createStandardGame();
    GameResult result = game.executeMove(new Move(new Position(5, 0), new Position(6, 1)));
    assertInstanceOf(GameResult.Invalid.class, result);
  }
}