package edu.austral.dissis.checkers.game;

import edu.austral.dissis.checkers.model.CheckersExtra;
import edu.austral.dissis.checkers.rules.CheckersBoardUpdater;
import edu.austral.dissis.checkers.rules.CheckersMoveValidator;
import edu.austral.dissis.common.game.Game;
import edu.austral.dissis.common.model.Board;
import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.GameStatus;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.MoveResult;
import edu.austral.dissis.common.model.Piece;
import edu.austral.dissis.common.rules.MoveValidator;
import edu.austral.dissis.common.rules.TurnManager;
import edu.austral.dissis.common.rules.WinCondition;

public class CheckersGame extends Game {

  private final CheckersBoardUpdater boardUpdater;
  private final CheckersMoveValidator checkersMoveValidator;

  public CheckersGame(
      GameState initialState,
      MoveValidator moveValidator,
      WinCondition winCondition,
      TurnManager turnManager,
      CheckersBoardUpdater boardUpdater) {
    super(initialState, moveValidator, winCondition, turnManager);
    this.boardUpdater = boardUpdater;
    this.checkersMoveValidator = new CheckersMoveValidator();
  }

  @Override
  public MoveResult executeMove(Move move) {
    if (currentState().isOver()) {
      return new MoveResult.Failure("El juego ya terminó");
    }
    var violation = moveValidator.findViolation(move, currentState());
    if (violation.isPresent()) {
      return new MoveResult.Failure(violation.get());
    }
    Board boardBefore = currentState().board();
    boolean wasCapture = boardUpdater.wasCapture(move, boardBefore);
    Board newBoard = boardUpdater.apply(move, boardBefore, currentState().currentPlayer());
    GameState newState = buildNextState(move, newBoard, wasCapture);
    commitState(newState);
    return new MoveResult.Success(newState);
  }

  private GameState buildNextState(Move move, Board newBoard, boolean wasCapture) {
    Color currentPlayer = currentState().currentPlayer();
    if (wasCapture && canContinueCapturing(move.to(), newBoard, currentPlayer)) {
      GameState midState =
          new GameState(
              newBoard, currentPlayer, GameStatus.IN_PROGRESS, CheckersExtra.multiJump(move.to()));
      return midState;
    }
    Color nextPlayer = turnManager.nextPlayer(currentPlayer, currentState());
    GameState tentative =
        new GameState(newBoard, nextPlayer, GameStatus.IN_PROGRESS, CheckersExtra.none());
    GameStatus status = winCondition.evaluate(tentative, move);
    return new GameState(newBoard, nextPlayer, status, CheckersExtra.none());
  }

  private boolean canContinueCapturing(
      edu.austral.dissis.common.model.Position pos, Board newBoard, Color currentPlayer) {
    Piece piece = newBoard.pieceAt(pos).orElse(null);
    if (piece == null) {
      return false;
    }
    GameState tempState =
        new GameState(newBoard, currentPlayer, GameStatus.IN_PROGRESS, CheckersExtra.none());
    return checkersMoveValidator.pieceCanCapture(pos, piece, tempState);
  }
}
