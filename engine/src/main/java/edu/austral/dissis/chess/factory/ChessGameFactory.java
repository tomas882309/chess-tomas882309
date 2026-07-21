package edu.austral.dissis.chess.factory;

import edu.austral.dissis.chess.model.CastlingExtraState;
import edu.austral.dissis.chess.model.CastlingRights;
import edu.austral.dissis.chess.model.EnPassantExtraState;
import edu.austral.dissis.chess.rules.board.CastlingBoardEffect;
import edu.austral.dissis.chess.rules.board.EnPassantBoardEffect;
import edu.austral.dissis.chess.rules.board.PromotionBoardEffect;
import edu.austral.dissis.chess.rules.move.CastlingValidator;
import edu.austral.dissis.chess.rules.move.KingInCheckValidator;
import edu.austral.dissis.common.rules.move.BehaviourValidator;
import edu.austral.dissis.chess.rules.win.CheckmateWinCondition;
import edu.austral.dissis.common.game.Game;
import edu.austral.dissis.common.game.GameEngine;
import edu.austral.dissis.common.game.GameEngineImpl;
import edu.austral.dissis.common.model.*;
import edu.austral.dissis.common.model.state.CompositeExtraState;
import edu.austral.dissis.common.model.state.GameExtraState;
import edu.austral.dissis.common.rules.board.BoardUpdater;
import edu.austral.dissis.common.rules.move.*;
import edu.austral.dissis.common.rules.turn.StandardTurnManager;
import edu.austral.dissis.common.rules.turn.TurnManager;
import edu.austral.dissis.common.rules.win.WinCondition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ChessGameFactory {

    public static Game createStandardGame() {
        MoveValidator validator = buildValidator();
        GameEngine engine = buildEngine(validator, new CheckmateWinCondition(validator), new StandardTurnManager());
        GameState initialState = new GameState(buildInitialBoard(), Color.WHITE, GameStatus.IN_PROGRESS, buildInitialExtraState());
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

    private static GameExtraState buildInitialExtraState(){
        return new CompositeExtraState(List.of(new EnPassantExtraState(Optional.empty()), new CastlingExtraState(CastlingRights.allEnabled())));
    }

    private static GameEngine buildEngine(MoveValidator validator, WinCondition winCondition, TurnManager turnManager) {
        BoardUpdater updater = new BoardUpdater(List.of(new EnPassantBoardEffect(), new CastlingBoardEffect(), new PromotionBoardEffect()));
        return new GameEngineImpl(validator, winCondition, turnManager, updater);
    }

    public static Board buildInitialBoard() {
        Map<Position, Piece> pieces = new HashMap<>();
        placeBackRank(pieces, Color.WHITE, 0);
        placePawns(pieces, Color.WHITE, 1);
        placePawns(pieces, Color.BLACK, 6);
        placeBackRank(pieces, Color.BLACK, 7);
        return new Board(pieces, 8, 8);
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

    private static void placePawns(Map<Position, Piece> pieces, Color color, int row) {
        for (int col = 0; col < 8; col++) {
            pieces.put(new Position(row, col), ChessPieceFactory.pawn(color));
        }
    }

    public static Game createFromBoard(Board board, Color currentPlayer, CastlingRights rights, Optional<Position> enPassant) {
        MoveValidator validator = buildValidator();
        GameEngine engine = buildEngine(validator, new CheckmateWinCondition(validator), new StandardTurnManager());
        GameExtraState extraState = new CompositeExtraState(List.of(
                new EnPassantExtraState(enPassant),
                new CastlingExtraState(rights)
        ));
        GameState state = new GameState(board, currentPlayer, GameStatus.IN_PROGRESS, extraState);
        return new Game(engine, state, List.of(), List.of());
    }
}
