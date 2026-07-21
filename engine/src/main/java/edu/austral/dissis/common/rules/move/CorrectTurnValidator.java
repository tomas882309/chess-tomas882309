package edu.austral.dissis.common.rules.move;

import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;

import java.util.Optional;

public class CorrectTurnValidator implements MoveValidator{


    @Override
    public Optional<String> findViolation(Move move, GameState state) {
        return state.board().pieceAt(move.from())
                .filter(piece -> !piece.isColor(state.currentPlayer()))
                .map(piece -> Optional.of("No es tu turno"))
                .orElse(Optional.empty());
    }
}
