package edu.austral.dissis.common.game;

import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.MoveResult;
import edu.austral.dissis.common.rules.MoveValidator;
import edu.austral.dissis.common.rules.TurnManager;
import edu.austral.dissis.common.rules.WinCondition;
import java.util.ArrayDeque;
import java.util.Deque;

public abstract class Game {

  private GameState state;
  private final Deque<GameState> history = new ArrayDeque<>();
  private final Deque<GameState> redoStack = new ArrayDeque<>();

  protected final MoveValidator moveValidator;
  protected final WinCondition winCondition;
  protected final TurnManager turnManager;

  protected Game(
      GameState initialState,
      MoveValidator moveValidator,
      WinCondition winCondition,
      TurnManager turnManager) {
    this.state = initialState;
    this.moveValidator = moveValidator;
    this.winCondition = winCondition;
    this.turnManager = turnManager;
  }

  public abstract MoveResult executeMove(Move move);

  public MoveResult undo() {
    if (history.isEmpty()) {
      return new MoveResult.Failure("No hay movimientos para deshacer");
    }
    redoStack.push(state);
    state = history.pop();
    return new MoveResult.Success(state);
  }

  public MoveResult redo() {
    if (redoStack.isEmpty()) {
      return new MoveResult.Failure("No hay movimientos para rehacer");
    }
    history.push(state);
    state = redoStack.pop();
    return new MoveResult.Success(state);
  }

  public GameState currentState() {
    return state;
  }

  public boolean canUndo() {
    return !history.isEmpty();
  }

  public boolean canRedo() {
    return !redoStack.isEmpty();
  }

  protected void commitState(GameState newState) {
    history.push(state);
    redoStack.clear();
    state = newState;
  }
}
