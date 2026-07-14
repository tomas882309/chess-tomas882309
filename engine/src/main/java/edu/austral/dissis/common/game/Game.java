package edu.austral.dissis.common.game;

import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.MoveResult;

public class Game {

  private final GameEngine engine;
  private final GameHistory history;

  public Game(GameEngine engine, GameState initialState) {
    this.engine = engine;
    this.history = new GameHistory(initialState);
  }

  public MoveResult executeMove(Move move) {
    MoveResult result = engine.executeMove(move, history.current());
    if (result instanceof MoveResult.Success s) {
      history.commit(s.newState());
    }
    return result;
  }

  public MoveResult undo() {
    return history
        .undo()
        .map(state -> (MoveResult) new MoveResult.Success(state))
        .orElse(new MoveResult.Failure("No hay movimientos para deshacer"));
  }

  public MoveResult redo() {
    return history
        .redo()
        .map(state -> (MoveResult) new MoveResult.Success(state))
        .orElse(new MoveResult.Failure("No hay movimientos para rehacer"));
  }

  public GameState currentState() {
    return history.current();
  }

  public boolean canUndo() {
    return history.canUndo();
  }

  public boolean canRedo() {
    return history.canRedo();
  }
}
