package edu.austral.dissis.common.model.state;

import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;

public class EmptyExtraState implements GameExtraState {
    public static final EmptyExtraState INSTANCE = new EmptyExtraState();
    private EmptyExtraState() {}

    @Override
    public GameExtraState update(Move move, GameState state) {
        return this;
    }
}
