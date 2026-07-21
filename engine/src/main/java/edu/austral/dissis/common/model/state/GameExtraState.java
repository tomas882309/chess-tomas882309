package edu.austral.dissis.common.model.state;

import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;

import java.util.Optional;

public interface GameExtraState {
    GameExtraState update (Move move, GameState state);

    default <T extends GameExtraState> Optional<T> get(Class<T> type){
        return type.isInstance(this) ? Optional.of(type.cast(this)) : Optional.empty();
    }
}
