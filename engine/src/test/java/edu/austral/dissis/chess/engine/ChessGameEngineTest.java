package edu.austral.dissis.chess.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.austral.dissis.chess.factory.ChessPieceFactory;
import edu.austral.dissis.chess.factory.ChessGameFactory;
import edu.austral.dissis.chess.game.ChessGameEngine;
import edu.austral.dissis.chess.model.CastlingRights;
import edu.austral.dissis.chess.model.ChessExtra;
import edu.austral.dissis.chess.model.ChessMove;
import edu.austral.dissis.chess.model.pieces.King;
import edu.austral.dissis.chess.model.pieces.Queen;
import edu.austral.dissis.chess.rules.BoardUpdater;
import edu.austral.dissis.chess.rules.CheckmateWinCondition;
import edu.austral.dissis.chess.rules.ChessMoveValidator;
import edu.austral.dissis.chess.rules.NextStateBuilder;
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
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ChessGameEngineTest {

    private Game buildGame(Map<Position, Piece> pieces, Color currentPlayer,
                           CastlingRights rights, Optional<Position> enPassant) {
        Board board = new Board(pieces, 8);
        GameState state = new GameState(board, currentPlayer, GameStatus.IN_PROGRESS,
                new ChessExtra(rights, enPassant));
        ChessGameEngine engine = new ChessGameEngine(new ChessMoveValidator(),
                new CheckmateWinCondition(), new StandardTurnManager(), new BoardUpdater(), new NextStateBuilder());
        return new Game(engine, state);
    }

    private Game buildGame(Map<Position, Piece> pieces) {
        return buildGame(pieces, Color.WHITE, CastlingRights.noneEnabled(), Optional.empty());
    }

    @Test
    void whitePawnCanMoveForward() {
        Game game = ChessGameFactory.createStandardGame();
        MoveResult result = game.executeMove(ChessMove.standard(new Position(1, 0), new Position(2, 0)));
        assertInstanceOf(MoveResult.Success.class, result);
    }

    @Test
    void whitePawnCanMoveDoubleFromStart() {
        Game game = ChessGameFactory.createStandardGame();
        MoveResult result = game.executeMove(ChessMove.standard(new Position(1, 0), new Position(3, 0)));
        assertInstanceOf(MoveResult.Success.class, result);
    }

    @Test
    void blackCannotMoveFirst() {
        Game game = ChessGameFactory.createStandardGame();
        MoveResult result = game.executeMove(ChessMove.standard(new Position(6, 0), new Position(5, 0)));
        assertInstanceOf(MoveResult.Failure.class, result);
    }

    @Test
    void knightCanJumpOverPieces() {
        Game game = ChessGameFactory.createStandardGame();
        MoveResult result = game.executeMove(ChessMove.standard(new Position(0, 1), new Position(2, 0)));
        assertInstanceOf(MoveResult.Success.class, result);
    }

    @Test
    void cannotCaptureOwnPiece() {
        Game game = buildGame(Map.of(
                new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                new Position(1, 4), ChessPieceFactory.pawn(Color.WHITE),
                new Position(7, 4), ChessPieceFactory.king(Color.BLACK)));
        MoveResult result = game.executeMove(ChessMove.standard(new Position(0, 4), new Position(1, 4)));
        assertInstanceOf(MoveResult.Failure.class, result);
    }

    @Test
    void pawnCanCaptureDiagonally() {
        Game game = buildGame(Map.of(
                new Position(4, 4), ChessPieceFactory.pawn(Color.WHITE),
                new Position(5, 5), ChessPieceFactory.pawn(Color.BLACK),
                new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                new Position(7, 4), ChessPieceFactory.king(Color.BLACK)));
        MoveResult result = game.executeMove(ChessMove.standard(new Position(4, 4), new Position(5, 5)));
        assertInstanceOf(MoveResult.Success.class, result);
    }

    @Test
    void castlingKingsideMovesKingAndRook() {
        Game game = buildGame(Map.of(
                        new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                        new Position(0, 7), ChessPieceFactory.rook(Color.WHITE),
                        new Position(7, 4), ChessPieceFactory.king(Color.BLACK)),
                Color.WHITE, CastlingRights.allEnabled(), Optional.empty());
        MoveResult.Success result = (MoveResult.Success) game.executeMove(
                ChessMove.standard(new Position(0, 4), new Position(0, 6)));
        assertTrue(result.newState().board().pieceAt(new Position(0, 6)).isPresent());
        assertTrue(result.newState().board().pieceAt(new Position(0, 5)).isPresent());
    }

    @Test
    void enPassantRemovesCapturedPawn() {
        Game game = buildGame(Map.of(
                        new Position(4, 3), ChessPieceFactory.pawn(Color.WHITE),
                        new Position(4, 4), ChessPieceFactory.pawn(Color.BLACK),
                        new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                        new Position(7, 4), ChessPieceFactory.king(Color.BLACK)),
                Color.WHITE, CastlingRights.noneEnabled(), Optional.of(new Position(5, 4)));
        MoveResult.Success result = (MoveResult.Success) game.executeMove(
                ChessMove.standard(new Position(4, 3), new Position(5, 4)));
        assertTrue(result.newState().board().pieceAt(new Position(4, 4)).isEmpty());
    }

    @Test
    void pawnPromotesToQueenAutomatically() {
        Game game = buildGame(Map.of(
                new Position(6, 3), ChessPieceFactory.pawn(Color.WHITE),
                new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                new Position(7, 7), ChessPieceFactory.king(Color.BLACK)));
        MoveResult.Success result = (MoveResult.Success) game.executeMove(
                ChessMove.standard(new Position(6, 3), new Position(7, 3)));
        Piece promoted = result.newState().board().pieceAt(new Position(7, 3)).get();
        assertTrue(promoted.isType(Queen.INSTANCE));
    }

    @Test
    void cannotMoveIntoCheck() {
        Game game = buildGame(Map.of(
                new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                new Position(3, 4), ChessPieceFactory.rook(Color.BLACK),
                new Position(7, 4), ChessPieceFactory.king(Color.BLACK)));
        MoveResult result = game.executeMove(ChessMove.standard(new Position(0, 4), new Position(1, 4)));
        assertInstanceOf(MoveResult.Failure.class, result);
    }

    @Test
    void foolsMateResultsInCheckmate() {
        Game game = ChessGameFactory.createStandardGame();
        game.executeMove(ChessMove.standard(new Position(1, 5), new Position(2, 5)));
        game.executeMove(ChessMove.standard(new Position(6, 4), new Position(4, 4)));
        game.executeMove(ChessMove.standard(new Position(1, 6), new Position(3, 6)));
        MoveResult.Success result = (MoveResult.Success) game.executeMove(
                ChessMove.standard(new Position(7, 3), new Position(3, 7)));
        assertEquals(GameStatus.BLACK_WINS, result.newState().status());
    }

    @Test
    void undoRestoresPreviousBoard() {
        Game game = ChessGameFactory.createStandardGame();
        game.executeMove(ChessMove.standard(new Position(1, 0), new Position(2, 0)));
        MoveResult.Success undo = (MoveResult.Success) game.undo();
        assertTrue(undo.newState().board().pieceAt(new Position(1, 0)).isPresent());
        assertTrue(undo.newState().board().pieceAt(new Position(2, 0)).isEmpty());
    }

    @Test
    void kingCannotMoveToSquareStillUnderAttack() {
        Game game = buildGame(Map.of(
                new Position(0, 4), ChessPieceFactory.king(Color.WHITE),
                new Position(3, 4), ChessPieceFactory.queen(Color.BLACK),
                new Position(7, 0), ChessPieceFactory.king(Color.BLACK)));
        MoveResult result = game.executeMove(ChessMove.standard(new Position(0, 4), new Position(1, 4)));
        assertInstanceOf(MoveResult.Failure.class, result);
    }
}