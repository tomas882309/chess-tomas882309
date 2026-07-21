package edu.austral.dissis.common.rules.move;

import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;

import java.util.Optional;

public class DestinationInBoundsValidator implements MoveValidator{


    @Override
    public Optional<String> findViolation(Move move, GameState state) {

        if(!state.board().isWithinBounds(move.to())){
            return Optional.of("Destino fuera del tablero");
        }
        return Optional.empty();
    }
}
