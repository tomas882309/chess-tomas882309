package edu.austral.dissis.common.rules.move;

import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;

import java.util.Optional;

public class BehaviourValidator implements MoveValidator {

    @Override
    public Optional<String> findViolation(Move move, GameState state) {
        return state.board().pieceAt(move.from())
                .filter(piece -> !piece.moveBehaviour().isValidMove(move, piece, state))
                .map(piece -> Optional.of("Movimiento invalido para esta pieza"))
                .orElse(Optional.empty());
    }
}
