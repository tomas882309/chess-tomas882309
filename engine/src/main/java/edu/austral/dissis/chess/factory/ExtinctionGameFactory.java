package edu.austral.dissis.chess.factory;

import edu.austral.dissis.chess.model.CastlingRights;
import edu.austral.dissis.chess.rules.board.CastlingBoardEffect;
import edu.austral.dissis.chess.rules.board.EnPassantBoardEffect;
import edu.austral.dissis.chess.rules.board.PromotionBoardEffect;
import edu.austral.dissis.common.rules.move.BehaviourValidator;
import edu.austral.dissis.chess.rules.move.KingInCheckValidator;
import edu.austral.dissis.chess.rules.move.CastlingValidator;
import edu.austral.dissis.chess.model.EnPassantExtraState;
import edu.austral.dissis.chess.model.CastlingExtraState;
import edu.austral.dissis.common.model.state.CompositeExtraState;
import edu.austral.dissis.common.model.state.GameExtraState;
import edu.austral.dissis.extinction.rules.ExtinctionWinCondition;
import edu.austral.dissis.common.game.Game;
import edu.austral.dissis.common.game.GameEngineImpl;
import edu.austral.dissis.common.model.*;
import edu.austral.dissis.common.rules.board.BoardUpdater;
import edu.austral.dissis.common.rules.move.*;
import edu.austral.dissis.common.rules.turn.StandardTurnManager;

import java.util.List;
import java.util.Optional;

public class ExtinctionGameFactory {

    public static Game createStandardGame() {
        MoveValidator validator = new CompositeValidator(List.of(
                new PieceExistsValidator(),
                new CorrectTurnValidator(),
                new DestinationInBoundsValidator(),
                new BehaviourValidator(),
                new CastlingValidator(),
                new KingInCheckValidator()
        ));
        GameEngineImpl engine = new GameEngineImpl(
                validator,
                new ExtinctionWinCondition(),
                new StandardTurnManager(),
                new BoardUpdater(List.of(new EnPassantBoardEffect(), new CastlingBoardEffect(), new PromotionBoardEffect()))
        );
        GameExtraState extraState = new CompositeExtraState(List.of(
                new EnPassantExtraState(Optional.empty()),
                new CastlingExtraState(CastlingRights.allEnabled())
        ));
        GameState initialState = new GameState(
                ChessGameFactory.buildInitialBoard(),
                Color.WHITE,
                GameStatus.IN_PROGRESS,
                extraState
        );
        return new Game(engine, initialState, List.of(), List.of());
    }

    public static Game createFromBoard(Board board, Color currentPlayer) {
        MoveValidator validator = new CompositeValidator(List.of(
                new PieceExistsValidator(),
                new CorrectTurnValidator(),
                new DestinationInBoundsValidator(),
                new BehaviourValidator(),
                new CastlingValidator(),
                new KingInCheckValidator()
        ));
        GameEngineImpl engine = new GameEngineImpl(
                validator,
                new ExtinctionWinCondition(),
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