package edu.austral.dissis.checkers.game;

import edu.austral.dissis.checkers.model.CheckersExtra;
import edu.austral.dissis.checkers.rules.CheckersBoardUpdater;
import edu.austral.dissis.checkers.rules.CheckersMoveValidator;
import edu.austral.dissis.common.game.GameEngine;
import edu.austral.dissis.common.model.Board;
import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.GameStatus;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.MoveResult;
import edu.austral.dissis.common.model.Position;
import edu.austral.dissis.common.rules.TurnManager;
import edu.austral.dissis.common.rules.WinCondition;

public class CheckersGameEngine implements GameEngine {

  private final CheckersMoveValidator moveValidator;
  private final WinCondition winCondition;
  private final TurnManager turnManager;
  private final CheckersBoardUpdater boardUpdater;

  public CheckersGameEngine(
      CheckersMoveValidator moveValidator,
      WinCondition winCondition,
      TurnManager turnManager,
      CheckersBoardUpdater boardUpdater) {
    this.moveValidator = moveValidator;
    this.winCondition = winCondition;
    this.turnManager = turnManager;
    this.boardUpdater = boardUpdater;
  }

  @Override
  public MoveResult executeMove(Move move, GameState state) {
    if (state.isOver()) {
      return new MoveResult.Failure("El juego ya terminó");
    }
    var violation = moveValidator.findViolation(move, state);
    if (violation.isPresent()) {
      return new MoveResult.Failure(violation.get());
    }
    boolean wasCapture = boardUpdater.wasCapture(move, state.board());
    Board newBoard = boardUpdater.apply(move, state.board(), state.currentPlayer());
    return new MoveResult.Success(buildNextState(move, state, newBoard, wasCapture));
  }

  private GameState buildNextState(Move move, GameState state, Board newBoard, boolean wasCapture) {
    if (wasCapture && canContinueCapturing(move.to(), newBoard, state.currentPlayer())) {
      return new GameState(
          newBoard,
          state.currentPlayer(),
          GameStatus.IN_PROGRESS,
          CheckersExtra.multiJump(move.to()));
    }
    return buildTurnTransitionState(move, state, newBoard);
  }

  private GameState buildTurnTransitionState(Move move, GameState state, Board newBoard) {
    Color nextPlayer = turnManager.nextPlayer(state.currentPlayer(), state);
    GameState tentative =
        new GameState(newBoard, nextPlayer, GameStatus.IN_PROGRESS, CheckersExtra.none());
    GameStatus status = winCondition.evaluate(tentative, move);
    return new GameState(newBoard, nextPlayer, status, CheckersExtra.none());
  }

  private boolean canContinueCapturing(Position pos, Board newBoard, Color currentPlayer) {
    GameState tempState =
        new GameState(newBoard, currentPlayer, GameStatus.IN_PROGRESS, CheckersExtra.none());
    return newBoard
        .pieceAt(pos)
        .map(piece -> moveValidator.pieceCanCapture(pos, piece, tempState))
        .orElse(false);
  }
}
