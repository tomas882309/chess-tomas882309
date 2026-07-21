package edu.austral.dissis.chess.engine;

import edu.austral.dissis.chess.factory.ChessGameFactory;
import edu.austral.dissis.chess.factory.ChessPieceFactory;
import edu.austral.dissis.chess.model.CastlingRights;
import edu.austral.dissis.chess.model.pieces.Queen;
import edu.austral.dissis.common.game.Game;
import edu.austral.dissis.common.game.GameResult;
import edu.austral.dissis.common.model.*;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ChessGameEngineTest {

    private Game buildGame(Map<Position, Piece> pieces, Color currentPlayer,
                           CastlingRights rights, Optional<Position> enPassant) {
        return ChessGameFactory.createFromBoard(new Board(pieces, 8, 8), currentPlayer, rights, enPassant);
    }

    private Game buildGame(Map<Position, Piece> pieces) {
        return buildGame(pieces, Color.WHITE, new CastlingRights(false, false, false, false), Optional.empty());
    }

    @Test
    void whitePawnCanMoveForward() {
        Game game = ChessGameFactory.createStandardGame();
        GameResult result = game.executeMove(new Move(new Position(1, 0), new Position(2, 0)));
        assertInstanceOf(GameResult.Moved.class, result);
    }

    @Test
    void whitePawnCanMoveDoubleFromStart() {
        Game game = ChessGameFactory.createStandardGame();
        GameResult result = game.executeMove(new Move(new Position(1, 0), new Position(3, 0)));
        assertInstanceOf(GameResult.Moved.class, result);
    }

    @Test
    void blackCannotMoveFirst() {
        Game game = ChessGameFactory.createStandardGame();
        GameResult result = game.executeMove(new Move(new Position(6, 0), new Position(5, 0)));
        assertInstanceOf(GameResult.Invalid.class, result);
    }

    @Test
    void knightCanJumpOverPieces() {
        Game game = ChessGameFactory.createStandardGame();
        GameResult result = game.executeMove(new Move(new Position(0, 1), new Position(2, 0)));
        assertInstanceOf(GameResult.Moved.class, result);
    }

    @Test
    void cannotCaptureOwnPiece() {
        Game game = buildGame(Map.of(
                new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                new Position(1, 4), ChessPieceFactory.pawn(Color.WHITE),
                new Position(7, 4), ChessPieceFactory.king(Color.BLACK)));
        GameResult result = game.executeMove(new Move(new Position(0, 4), new Position(1, 4)));
        assertInstanceOf(GameResult.Invalid.class, result);
    }

    @Test
    void pawnCanCaptureDiagonally() {
        Game game = buildGame(Map.of(
                new Position(4, 4), ChessPieceFactory.pawn(Color.WHITE),
                new Position(5, 5), ChessPieceFactory.pawn(Color.BLACK),
                new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                new Position(7, 4), ChessPieceFactory.king(Color.BLACK)));
        GameResult result = game.executeMove(new Move(new Position(4, 4), new Position(5, 5)));
        assertInstanceOf(GameResult.Moved.class, result);
    }

    @Test
    void castlingKingsideMovesKingAndRook() {
        Game game = buildGame(Map.of(
                        new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                        new Position(0, 7), ChessPieceFactory.rook(Color.WHITE),
                        new Position(7, 4), ChessPieceFactory.king(Color.BLACK)),
                Color.WHITE, CastlingRights.allEnabled(), Optional.empty());
        GameResult.Moved result = (GameResult.Moved) game.executeMove(
                new Move(new Position(0, 4), new Position(0, 6)));
        Board newBoard = result.newGame().current().board();
        assertTrue(newBoard.pieceAt(new Position(0, 6)).isPresent());
        assertTrue(newBoard.pieceAt(new Position(0, 5)).isPresent());
    }

    @Test
    void enPassantRemovesCapturedPawn() {
        Game game = buildGame(Map.of(
                        new Position(4, 3), ChessPieceFactory.pawn(Color.WHITE),
                        new Position(4, 4), ChessPieceFactory.pawn(Color.BLACK),
                        new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                        new Position(7, 4), ChessPieceFactory.king(Color.BLACK)),
                Color.WHITE, new CastlingRights(false, false, false, false), Optional.of(new Position(5, 4)));
        GameResult.Moved result = (GameResult.Moved) game.executeMove(
                new Move(new Position(4, 3), new Position(5, 4)));
        assertTrue(result.newGame().current().board().pieceAt(new Position(4, 4)).isEmpty());
    }

    @Test
    void pawnPromotesToQueen() {
        Game game = buildGame(Map.of(
                new Position(6, 3), ChessPieceFactory.pawn(Color.WHITE),
                new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                new Position(7, 7), ChessPieceFactory.king(Color.BLACK)));
        GameResult.Moved result = (GameResult.Moved) game.executeMove(
                new Move(new Position(6, 3), new Position(7, 3)));
        Piece promoted = result.newGame().current().board().pieceAt(new Position(7, 3)).get();
        assertTrue(promoted.isType(Queen.INSTANCE));
    }

    @Test
    void cannotMoveIntoCheck() {
        Game game = buildGame(Map.of(
                new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                new Position(3, 4), ChessPieceFactory.rook(Color.BLACK),
                new Position(7, 4), ChessPieceFactory.king(Color.BLACK)));
        GameResult result = game.executeMove(new Move(new Position(0, 4), new Position(1, 4)));
        assertInstanceOf(GameResult.Invalid.class, result);
    }

    @Test
    void foolsMateResultsInCheckmate() {
        Game game = ChessGameFactory.createStandardGame();
        game = ((GameResult.Moved) game.executeMove(new Move(new Position(1, 5), new Position(2, 5)))).newGame();
        game = ((GameResult.Moved) game.executeMove(new Move(new Position(6, 4), new Position(4, 4)))).newGame();
        game = ((GameResult.Moved) game.executeMove(new Move(new Position(1, 6), new Position(3, 6)))).newGame();
        GameResult.Moved result = (GameResult.Moved) game.executeMove(
                new Move(new Position(7, 3), new Position(3, 7)));
        assertEquals(GameStatus.BLACK_WINS, result.newGame().current().status());
    }

    @Test
    void undoRestoresPreviousBoard() {
        Game game = ChessGameFactory.createStandardGame();
        game = ((GameResult.Moved) game.executeMove(new Move(new Position(1, 0), new Position(2, 0)))).newGame();
        Game undone = game.undo();
        assertTrue(undone.current().board().pieceAt(new Position(1, 0)).isPresent());
        assertTrue(undone.current().board().pieceAt(new Position(2, 0)).isEmpty());
    }

    @Test
    void kingCannotMoveToSquareUnderAttack() {
        Game game = buildGame(Map.of(
                new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                new Position(3, 4), ChessPieceFactory.queen(Color.BLACK),
                new Position(7, 0), ChessPieceFactory.king(Color.BLACK)));
        GameResult result = game.executeMove(new Move(new Position(0, 4), new Position(1, 4)));
        assertInstanceOf(GameResult.Invalid.class, result);
    }
}