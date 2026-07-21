package edu.austral.dissis.common.game;

import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.MoveResult;

import java.util.ArrayList;
import java.util.List;

public record Game(GameEngine engine, GameState current, List<GameState> past, List<GameState> future) {

    public GameResult executeMove(Move move) {
        MoveResult result = engine.executeMove(move, current);
        return switch (result) {
            case MoveResult.Success s -> new GameResult.Moved(new Game(engine, s.newState(), addTo(past, current), List.of()));
            case MoveResult.Failure f -> new GameResult.Invalid(f.reason());
        };
    }

    public Game undo() {
        if (!canUndo()) return this;
        return new Game(engine, last(past), removeLast(past), addTo(future, current));
    }

    public Game redo() {
        if (!canRedo()) return this;
        return new Game(engine, last(future), addTo(past, current), removeLast(future));
    }

    public boolean canUndo() { return !past.isEmpty(); }
    public boolean canRedo() { return !future.isEmpty(); }

    private List<GameState> addTo(List<GameState> list, GameState state) {
        List<GameState> newList = new ArrayList<>(list);
        newList.add(state);
        return List.copyOf(newList);
    }

    private List<GameState> removeLast(List<GameState> list) {
        List<GameState> newList = new ArrayList<>(list);
        newList.removeLast();
        return List.copyOf(newList);
    }

    private GameState last(List<GameState> list) {
        return list.get(list.size() - 1);
    }

}
