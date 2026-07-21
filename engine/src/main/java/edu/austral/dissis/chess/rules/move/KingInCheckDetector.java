package edu.austral.dissis.chess.rules.move;

import edu.austral.dissis.chess.model.pieces.King;
import edu.austral.dissis.common.model.*;

import java.util.Map;

public class KingInCheckDetector {

    public boolean isKingInCheck(Color color, GameState state){
        Position kingPosition = findKingPosition(color, state.board());
        return isPositionThreatened(kingPosition, color, state);
    }

    private Position findKingPosition (Color color, Board board){
        return board.pieces().entrySet().stream()
                .filter(e -> e.getValue().isColor(color) && e.getValue().isType(King.INSTANCE))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow();
    }

    private boolean isPositionThreatened(Position pos, Color allyColor, GameState state){
        return state.board().pieces().entrySet().stream()
                .filter(e -> !e.getValue().isColor(allyColor))
                .anyMatch(e -> e.getValue().moveBehaviour().isValidMove(new Move(e.getKey(), pos), e.getValue(), state));
    }
}
