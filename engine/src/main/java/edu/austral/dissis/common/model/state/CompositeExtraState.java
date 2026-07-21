package edu.austral.dissis.common.model.state;

import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;

import java.util.List;
import java.util.Optional;

public class CompositeExtraState implements GameExtraState {

    private final List<GameExtraState> states;

    public CompositeExtraState(List<GameExtraState> states){
        this.states = List.copyOf(states);
    }

    @Override
    public GameExtraState update(Move move, GameState state) {
        List<GameExtraState> updated = states.stream()
                .map(s -> s.update(move, state))
                .toList();
        return new CompositeExtraState(updated);
    }

    @Override
    public <T extends GameExtraState> Optional<T> get(Class<T> type){
        return states.stream()
                .map(s -> s.get(type))
                .filter(Optional::isPresent)
                .findFirst()
                .orElse(Optional.empty());
    }
}
