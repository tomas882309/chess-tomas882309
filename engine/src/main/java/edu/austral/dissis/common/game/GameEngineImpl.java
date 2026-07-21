package edu.austral.dissis.common.game;

import edu.austral.dissis.common.model.*;
import edu.austral.dissis.common.model.state.GameExtraState;
import edu.austral.dissis.common.rules.board.BoardUpdater;
import edu.austral.dissis.common.rules.move.MoveValidator;
import edu.austral.dissis.common.rules.turn.TurnManager;
import edu.austral.dissis.common.rules.win.WinCondition;

import java.util.Optional;

public class GameEngineImpl implements GameEngine {

    private final MoveValidator moveValidator;
    private final WinCondition winCondition;
    private final TurnManager turnManager;
    private final BoardUpdater boardUpdater;

    public GameEngineImpl(MoveValidator moveValidator, WinCondition winCondition,
                          TurnManager turnManager, BoardUpdater boardUpdater) {
        this.moveValidator = moveValidator;
        this.winCondition = winCondition;
        this.turnManager = turnManager;
        this.boardUpdater = boardUpdater;
    }

    @Override
    public MoveResult executeMove(Move move, GameState state) {
        if (state.isOver()){
            return new MoveResult.Failure("El juego ya terminó");
        }
        Optional<String> violation = moveValidator.findViolation(move, state);
        if (violation.isPresent()) {
            return new MoveResult.Failure(violation.get());
        }
        Board newBoard = boardUpdater.apply(move, state);
        return new MoveResult.Success(buildNextState(move, state, newBoard));
    }

    private GameState buildNextState(Move move, GameState state, Board newBoard) {
        GameExtraState newExtra = state.extraState().update(move, state);
        GameState withNewExtra = new GameState(newBoard, state.currentPlayer(), GameStatus.IN_PROGRESS, newExtra);
        Color nextPlayer = turnManager.nextPlayer(state.currentPlayer(), withNewExtra);
        GameState temporal = new GameState(newBoard, nextPlayer, GameStatus.IN_PROGRESS, newExtra);
        GameStatus status = winCondition.evaluate(temporal);
        return new GameState(newBoard, nextPlayer, status, newExtra);
    }
}