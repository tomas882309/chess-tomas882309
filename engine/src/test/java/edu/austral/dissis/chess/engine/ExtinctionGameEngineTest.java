package edu.austral.dissis.chess.engine;

import edu.austral.dissis.chess.factory.ChessPieceFactory;
import edu.austral.dissis.chess.factory.ExtinctionGameFactory;
import edu.austral.dissis.common.game.Game;
import edu.austral.dissis.common.game.GameResult;
import edu.austral.dissis.common.model.*;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExtinctionGameEngineTest {

    private Game buildGame(Map<Position, Piece> pieces) {
        return ExtinctionGameFactory.createFromBoard(new Board(pieces, 8, 8), Color.WHITE);
    }

    @Test
    void normalMoveIsValid() {
        Game game = buildGame(Map.of(
                new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                new Position(1, 0), ChessPieceFactory.pawn(Color.WHITE),
                new Position(7, 4), ChessPieceFactory.king(Color.BLACK),
                new Position(6, 0), ChessPieceFactory.pawn(Color.BLACK)));
        GameResult result = game.executeMove(new Move(new Position(1, 0), new Position(2, 0)));
        assertInstanceOf(GameResult.Moved.class, result);
    }

    @Test
    void capturingLastQueenWins() {
        Game game = buildGame(Map.of(
                new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                new Position(1, 0), ChessPieceFactory.queen(Color.WHITE),
                new Position(7, 4), ChessPieceFactory.king(Color.BLACK),
                new Position(1, 1), ChessPieceFactory.queen(Color.BLACK)));
        GameResult.Moved result = (GameResult.Moved) game.executeMove(
                new Move(new Position(1, 0), new Position(1, 1)));
        assertEquals(GameStatus.WHITE_WINS, result.newGame().current().status());
    }

    @Test
    void capturingLastPawnWins() {
        Game game = buildGame(Map.of(
                new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                new Position(4, 4), ChessPieceFactory.rook(Color.WHITE),
                new Position(7, 4), ChessPieceFactory.king(Color.BLACK),
                new Position(4, 7), ChessPieceFactory.pawn(Color.BLACK)));
        GameResult.Moved result = (GameResult.Moved) game.executeMove(
                new Move(new Position(4, 4), new Position(4, 7)));
        assertEquals(GameStatus.WHITE_WINS, result.newGame().current().status());
    }

    @Test
    void capturingOneOfMultipleQueensDoesNotWin() {
        Game game = buildGame(Map.of(
                new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                new Position(4, 4), ChessPieceFactory.queen(Color.WHITE),
                new Position(7, 4), ChessPieceFactory.king(Color.BLACK),
                new Position(4, 6), ChessPieceFactory.queen(Color.BLACK),
                new Position(5, 7), ChessPieceFactory.queen(Color.BLACK)));
        GameResult.Moved result = (GameResult.Moved) game.executeMove(
                new Move(new Position(4, 4), new Position(4, 6)));
        assertEquals(GameStatus.IN_PROGRESS, result.newGame().current().status());
    }

    @Test
    void capturingLastKingWins() {
        Game game = buildGame(Map.of(
                new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                new Position(4, 4), ChessPieceFactory.rook(Color.WHITE),
                new Position(4, 7), ChessPieceFactory.king(Color.BLACK)));
        GameResult.Moved result = (GameResult.Moved) game.executeMove(
                new Move(new Position(4, 4), new Position(4, 7)));
        assertEquals(GameStatus.WHITE_WINS, result.newGame().current().status());
    }

    @Test
    void blackWinsWhenCapturingLastWhiteRook() {
        Game game = buildGame(Map.of(
                new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                new Position(4, 4), ChessPieceFactory.rook(Color.WHITE),
                new Position(7, 4), ChessPieceFactory.king(Color.BLACK),
                new Position(4, 7), ChessPieceFactory.rook(Color.BLACK)));
        game = ((GameResult.Moved) game.executeMove(
                new Move(new Position(0, 4), new Position(0, 3)))).newGame();
        GameResult.Moved result = (GameResult.Moved) game.executeMove(
                new Move(new Position(4, 7), new Position(4, 4)));
        assertEquals(GameStatus.BLACK_WINS, result.newGame().current().status());
    }

    @Test
    void undoRestoresPreviousBoard() {
        Game game = buildGame(Map.of(
                new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                new Position(1, 0), ChessPieceFactory.pawn(Color.WHITE),
                new Position(7, 4), ChessPieceFactory.king(Color.BLACK),
                new Position(6, 0), ChessPieceFactory.pawn(Color.BLACK)));
        game = ((GameResult.Moved) game.executeMove(
                new Move(new Position(1, 0), new Position(2, 0)))).newGame();
        Game undone = game.undo();
        assertTrue(undone.current().board().pieceAt(new Position(1, 0)).isPresent());
        assertTrue(undone.current().board().pieceAt(new Position(2, 0)).isEmpty());
    }
}