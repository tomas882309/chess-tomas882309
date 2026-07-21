package edu.austral.dissis.checkers.factory;

import edu.austral.dissis.checkers.model.MultiCaptureExtraState;
import edu.austral.dissis.checkers.rules.board.CaptureBoardEffect;
import edu.austral.dissis.checkers.rules.board.PromotionBoardEffect;
import edu.austral.dissis.checkers.rules.move.MandatoryCaptureValidator;
import edu.austral.dissis.checkers.rules.move.MultiCaptureOnlyValidator;
import edu.austral.dissis.checkers.rules.turn.CheckersTurnManager;
import edu.austral.dissis.checkers.rules.win.CheckersWinCondition;
import edu.austral.dissis.common.rules.move.BehaviourValidator;
import edu.austral.dissis.common.game.Game;
import edu.austral.dissis.common.game.GameEngineImpl;
import edu.austral.dissis.common.model.*;
import edu.austral.dissis.common.rules.board.BoardUpdater;
import edu.austral.dissis.common.rules.move.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CheckersGameFactory {

    public static Game createStandardGame() {
        MoveValidator validator = buildValidator();
        CheckersTurnManager turnManager = new CheckersTurnManager();
        CheckersWinCondition winCondition = new CheckersWinCondition(validator);
        BoardUpdater updater = new BoardUpdater(List.of(new CaptureBoardEffect(), new PromotionBoardEffect()));
        GameEngineImpl engine = new GameEngineImpl(validator, winCondition, turnManager, updater);
        GameState initialState = new GameState(
                buildInitialBoard(),
                Color.WHITE,
                GameStatus.IN_PROGRESS,
                new MultiCaptureExtraState(Optional.empty())
        );
        return new Game(engine, initialState, List.of(), List.of());
    }

    private static MoveValidator buildValidator() {
        return new CompositeValidator(List.of(
                new PieceExistsValidator(),
                new CorrectTurnValidator(),
                new DestinationInBoundsValidator(),
                new BehaviourValidator(),
                new MultiCaptureOnlyValidator(),
                new MandatoryCaptureValidator()
        ));
    }

    private static Board buildInitialBoard() {
        Map<Position, Piece> pieces = new HashMap<>();
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 8; col++)
                if ((row + col) % 2 == 0)
                    pieces.put(new Position(row, col), CheckersPieceFactory.man(Color.BLACK));
        for (int row = 5; row < 8; row++)
            for (int col = 0; col < 8; col++)
                if ((row + col) % 2 == 0)
                    pieces.put(new Position(row, col), CheckersPieceFactory.man(Color.WHITE));
        return new Board(pieces, 8, 8);
    }

    public static Game createFromBoard(Board board, Color currentPlayer) {
        MoveValidator validator = buildValidator();
        CheckersTurnManager turnManager = new CheckersTurnManager();
        CheckersWinCondition winCondition = new CheckersWinCondition(validator);
        BoardUpdater updater = new BoardUpdater(List.of(new CaptureBoardEffect(), new PromotionBoardEffect()));
        GameEngineImpl engine = new GameEngineImpl(validator, winCondition, turnManager, updater);
        GameState state = new GameState(board, currentPlayer, GameStatus.IN_PROGRESS, new MultiCaptureExtraState(Optional.empty()));
        return new Game(engine, state, List.of(), List.of());
    }
}