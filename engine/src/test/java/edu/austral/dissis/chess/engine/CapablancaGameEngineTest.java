package edu.austral.dissis.chess.engine;


import edu.austral.dissis.chess.factory.CapablancaGameFactory;
import edu.austral.dissis.chess.factory.CapablancaPieceFactory;
import edu.austral.dissis.chess.factory.ChessPieceFactory;
import edu.austral.dissis.common.game.Game;
import edu.austral.dissis.common.game.GameResult;
import edu.austral.dissis.common.model.*;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CapablancaGameEngineTest {

    private Game buildGame(Map<Position, Piece> pieces) {
        return CapablancaGameFactory.createFromBoard(new Board(pieces, 8, 10), Color.WHITE);
    }

    @Test
    void standardGameInitializesCorrectly() {
        Game game = CapablancaGameFactory.createStandardGame();
        Board board = game.current().board();
        assertEquals(8, board.rows());
        assertEquals(10, board.cols());
        assertFalse(board.pieces().isEmpty());
    }

    @Test
    void archbishopCanMoveDiagonally() {
        Game game = buildGame(Map.of(
                new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                new Position(4, 4), CapablancaPieceFactory.archbishop(Color.WHITE),
                new Position(7, 4), ChessPieceFactory.king(Color.BLACK)));
        GameResult result = game.executeMove(new Move(new Position(4, 4), new Position(6, 6)));
        assertInstanceOf(GameResult.Moved.class, result);
    }

    @Test
    void archbishopCanMoveAsKnight() {
        Game game = buildGame(Map.of(
                new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                new Position(4, 4), CapablancaPieceFactory.archbishop(Color.WHITE),
                new Position(7, 4), ChessPieceFactory.king(Color.BLACK)));
        GameResult result = game.executeMove(new Move(new Position(4, 4), new Position(6, 5)));
        assertInstanceOf(GameResult.Moved.class, result);
    }

    @Test
    void archbishopCannotMoveAsRook() {
        Game game = buildGame(Map.of(
                new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                new Position(4, 4), CapablancaPieceFactory.archbishop(Color.WHITE),
                new Position(7, 4), ChessPieceFactory.king(Color.BLACK)));
        GameResult result = game.executeMove(new Move(new Position(4, 4), new Position(4, 7)));
        assertInstanceOf(GameResult.Invalid.class, result);
    }

    @Test
    void archbishopCanJumpOverPiecesLikeKnight() {
        Game game = buildGame(Map.of(
                new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                new Position(4, 4), CapablancaPieceFactory.archbishop(Color.WHITE),
                new Position(5, 5), ChessPieceFactory.pawn(Color.WHITE),
                new Position(7, 4), ChessPieceFactory.king(Color.BLACK)));
        GameResult result = game.executeMove(new Move(new Position(4, 4), new Position(6, 5)));
        assertInstanceOf(GameResult.Moved.class, result);
    }

    @Test
    void chancellorCanMoveAsRook() {
        Game game = buildGame(Map.of(
                new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                new Position(4, 4), CapablancaPieceFactory.chancellor(Color.WHITE),
                new Position(7, 4), ChessPieceFactory.king(Color.BLACK)));
        GameResult result = game.executeMove(new Move(new Position(4, 4), new Position(4, 8)));
        assertInstanceOf(GameResult.Moved.class, result);
    }

    @Test
    void chancellorCanMoveAsKnight() {
        Game game = buildGame(Map.of(
                new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                new Position(4, 4), CapablancaPieceFactory.chancellor(Color.WHITE),
                new Position(7, 4), ChessPieceFactory.king(Color.BLACK)));
        GameResult result = game.executeMove(new Move(new Position(4, 4), new Position(6, 5)));
        assertInstanceOf(GameResult.Moved.class, result);
    }

    @Test
    void chancellorCannotMoveDiagonally() {
        Game game = buildGame(Map.of(
                new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                new Position(4, 4), CapablancaPieceFactory.chancellor(Color.WHITE),
                new Position(7, 4), ChessPieceFactory.king(Color.BLACK)));
        GameResult result = game.executeMove(new Move(new Position(4, 4), new Position(6, 6)));
        assertInstanceOf(GameResult.Invalid.class, result);
    }

    @Test
    void chancellorCanCaptureEnemy() {
        Game game = buildGame(Map.of(
                new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                new Position(4, 4), CapablancaPieceFactory.chancellor(Color.WHITE),
                new Position(7, 4), ChessPieceFactory.king(Color.BLACK),
                new Position(4, 7), ChessPieceFactory.pawn(Color.BLACK)));
        GameResult.Moved result = (GameResult.Moved) game.executeMove(
                new Move(new Position(4, 4), new Position(4, 7)));
        assertTrue(result.newGame().current().board().pieceAt(new Position(4, 7))
                .map(p -> p.isColor(Color.WHITE)).orElse(false));
    }

    @Test
    void archbishopCanCaptureEnemy() {
        Game game = buildGame(Map.of(
                new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                new Position(4, 4), CapablancaPieceFactory.archbishop(Color.WHITE),
                new Position(7, 4), ChessPieceFactory.king(Color.BLACK),
                new Position(6, 6), ChessPieceFactory.pawn(Color.BLACK)));
        GameResult.Moved result = (GameResult.Moved) game.executeMove(
                new Move(new Position(4, 4), new Position(6, 6)));
        assertTrue(result.newGame().current().board().pieceAt(new Position(6, 6))
                .map(p -> p.isColor(Color.WHITE)).orElse(false));
    }

    @Test
    void cannotCaptureOwnPiece() {
        Game game = buildGame(Map.of(
                new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                new Position(4, 4), CapablancaPieceFactory.chancellor(Color.WHITE),
                new Position(4, 7), ChessPieceFactory.pawn(Color.WHITE),
                new Position(7, 4), ChessPieceFactory.king(Color.BLACK)));
        GameResult result = game.executeMove(new Move(new Position(4, 4), new Position(4, 7)));
        assertInstanceOf(GameResult.Invalid.class, result);
    }

    @Test
    void undoRestoresPreviousBoard() {
        Game game = buildGame(Map.of(
                new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                new Position(4, 4), CapablancaPieceFactory.chancellor(Color.WHITE),
                new Position(7, 4), ChessPieceFactory.king(Color.BLACK)));
        game = ((GameResult.Moved) game.executeMove(
                new Move(new Position(4, 4), new Position(4, 7)))).newGame();
        Game undone = game.undo();
        assertTrue(undone.current().board().pieceAt(new Position(4, 4)).isPresent());
        assertTrue(undone.current().board().pieceAt(new Position(4, 7)).isEmpty());
    }
}