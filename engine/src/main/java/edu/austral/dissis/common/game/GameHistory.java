package edu.austral.dissis.common.game;

import edu.austral.dissis.common.model.GameState;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

public class GameHistory {

  private GameState current;
  private final Deque<GameState> past = new ArrayDeque<>();
  private final Deque<GameState> future = new ArrayDeque<>();

  public GameHistory(GameState initial) {
    this.current = initial;
  }

  public GameState current() {
    return current;
  }

  public void commit(GameState next) {
    past.push(current);
    future.clear();
    current = next;
  }

  public Optional<GameState> undo() {
    if (past.isEmpty()) {
      return Optional.empty();
    }
    future.push(current);
    current = past.pop();
    return Optional.of(current);
  }

  public Optional<GameState> redo() {
    if (future.isEmpty()) {
      return Optional.empty();
    }
    past.push(current);
    current = future.pop();
    return Optional.of(current);
  }

  public boolean canUndo() {
    return !past.isEmpty();
  }

  public boolean canRedo() {
    return !future.isEmpty();
  }
}
