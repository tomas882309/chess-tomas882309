package edu.austral.dissis.common.rules.move;

import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;

import java.util.List;
import java.util.Optional;

public class CompositeValidator implements MoveValidator{
    private final List<MoveValidator> validators;

    public CompositeValidator(List<MoveValidator> validators){
        this.validators = validators;
    }


    @Override
    public Optional<String> findViolation(Move move, GameState state) {
        return validators.stream()
                .map(v -> v.findViolation(move, state))
                .filter(Optional::isPresent)
                .findFirst()
                .orElse(Optional.empty());
    }
}
