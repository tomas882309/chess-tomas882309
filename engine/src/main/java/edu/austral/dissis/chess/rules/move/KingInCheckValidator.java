package edu.austral.dissis.chess.rules.move;

import edu.austral.dissis.chess.model.pieces.King;
import edu.austral.dissis.common.model.*;
import edu.austral.dissis.common.rules.move.MoveValidator;

import java.util.Map;
import java.util.Optional;

public class KingInCheckValidator implements MoveValidator {

    private final KingInCheckDetector detector = new KingInCheckDetector();

    @Override
    public Optional<String> findViolation(Move move, GameState state) {

        Board boardAfterMove = state.board().boardWithMove(move.from(), move.to());
        GameState stateAfterMove = new GameState(boardAfterMove, state.currentPlayer(), state.status(), state.extraState());

        if(detector.isKingInCheck(state.currentPlayer(), stateAfterMove)){
            return Optional.of("El movimiento deja al rey en jaque");
        }
        return Optional.empty();
    }

}
