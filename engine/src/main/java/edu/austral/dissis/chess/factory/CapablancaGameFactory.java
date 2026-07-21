package edu.austral.dissis.chess.factory;

import edu.austral.dissis.chess.model.CastlingRights;
import edu.austral.dissis.chess.model.EnPassantExtraState;
import edu.austral.dissis.chess.model.CastlingExtraState;
import edu.austral.dissis.chess.rules.board.CastlingBoardEffect;
import edu.austral.dissis.chess.rules.board.EnPassantBoardEffect;
import edu.austral.dissis.chess.rules.board.PromotionBoardEffect;
import edu.austral.dissis.common.rules.move.BehaviourValidator;
import edu.austral.dissis.chess.rules.move.CastlingValidator;
import edu.austral.dissis.chess.rules.move.KingInCheckValidator;
import edu.austral.dissis.chess.rules.win.CheckmateWinCondition;
import edu.austral.dissis.common.game.Game;
import edu.austral.dissis.common.game.GameEngineImpl;
import edu.austral.dissis.common.model.*;
import edu.austral.dissis.common.model.state.CompositeExtraState;
import edu.austral.dissis.common.model.state.GameExtraState;
import edu.austral.dissis.common.rules.board.BoardUpdater;
import edu.austral.dissis.common.rules.move.*;
import edu.austral.dissis.common.rules.turn.StandardTurnManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CapablancaGameFactory {

    public static Game createStandardGame() {
        MoveValidator validator = buildValidator();
        GameEngineImpl engine = new GameEngineImpl(
                validator,
                new CheckmateWinCondition(validator),
                new StandardTurnManager(),
                new BoardUpdater(List.of(new EnPassantBoardEffect(), new CastlingBoardEffect(), new PromotionBoardEffect()))
        );
        GameExtraState extraState = new CompositeExtraState(List.of(
                new EnPassantExtraState(Optional.empty()),
                new CastlingExtraState(CastlingRights.allEnabled())
        ));
        GameState initialState = new GameState(buildInitialBoard(), Color.WHITE, GameStatus.IN_PROGRESS, extraState);
        return new Game(engine, initialState, List.of(), List.of());
    }

    private static MoveValidator buildValidator() {
        return new CompositeValidator(List.of(
                new PieceExistsValidator(),
                new CorrectTurnValidator(),
                new DestinationInBoundsValidator(),
                new BehaviourValidator(),
                new CastlingValidator(),
                new KingInCheckValidator()
        ));
    }

    private static Board buildInitialBoard() {
        Map<Position, Piece> pieces = new HashMap<>();
        placeBackRank(pieces, Color.WHITE, 0);
        placeBackRank(pieces, Color.BLACK, 7);
        placePawns(pieces, Color.WHITE, 1);
        placePawns(pieces, Color.BLACK, 6);
        return new Board(pieces, 8, 10);
    }

    private static void placeBackRank(Map<Position, Piece> pieces, Color color, int row) {
        pieces.put(new Position(row, 0), ChessPieceFactory.rook(color));
        pieces.put(new Position(row, 1), ChessPieceFactory.knight(color));
        pieces.put(new Position(row, 2), CapablancaPieceFactory.archbishop(color));
        pieces.put(new Position(row, 3), ChessPieceFactory.queen(color));
        pieces.put(new Position(row, 4), ChessPieceFactory.king(color));
        pieces.put(new Position(row, 5), ChessPieceFactory.bishop(color));
        pieces.put(new Position(row, 6), ChessPieceFactory.bishop(color));
        pieces.put(new Position(row, 7), CapablancaPieceFactory.chancellor(color));
        pieces.put(new Position(row, 8), ChessPieceFactory.knight(color));
        pieces.put(new Position(row, 9), ChessPieceFactory.rook(color));
    }

    private static void placePawns(Map<Position, Piece> pieces, Color color, int row) {
        for (int col = 0; col < 10; col++)
            pieces.put(new Position(row, col), ChessPieceFactory.pawn(color));
    }

    public static Game createFromBoard(Board board, Color currentPlayer) {
        MoveValidator validator = buildValidator();
        GameEngineImpl engine = new GameEngineImpl(
                validator,
                new CheckmateWinCondition(validator),
                new StandardTurnManager(),
                new BoardUpdater(List.of(new EnPassantBoardEffect(), new CastlingBoardEffect(), new PromotionBoardEffect()))
        );
        GameExtraState extraState = new CompositeExtraState(List.of(
                new EnPassantExtraState(Optional.empty()),
                new CastlingExtraState(new CastlingRights(false, false, false, false))
        ));
        GameState state = new GameState(board, currentPlayer, GameStatus.IN_PROGRESS, extraState);
        return new Game(engine, state, List.of(), List.of());
    }
}