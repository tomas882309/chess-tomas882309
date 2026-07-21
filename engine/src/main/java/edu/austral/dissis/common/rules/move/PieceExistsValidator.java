package edu.austral.dissis.common.rules.move;

import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;

import java.util.Optional;

public class PieceExistsValidator implements MoveValidator{


    @Override
    public Optional<String> findViolation(Move move, GameState state) {
        if (state.board().pieceAt(move.from()).isEmpty()){
            return Optional.of("No hay pieza en la posicion de origen");
        }
        return Optional.empty();
    }
}
