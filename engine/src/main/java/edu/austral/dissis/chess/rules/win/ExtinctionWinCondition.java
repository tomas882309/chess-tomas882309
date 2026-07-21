package edu.austral.dissis.chess.rules.win;

import edu.austral.dissis.common.model.*;
import edu.austral.dissis.common.rules.win.WinCondition;

import java.util.Set;
import java.util.stream.Collectors;

public class ExtinctionWinCondition implements WinCondition {

    @Override
    public GameStatus evaluate(GameState state) {
        Color current = state.currentPlayer();
        Set<PieceKind> boardTypes = getAllTypes(state.board());
        Set<PieceKind> currentTypes = getTypesFor(state.board(), current);

        boolean extinct = boardTypes.stream().anyMatch(type -> !currentTypes.contains(type));
        if (extinct) {
            return current == Color.WHITE ? GameStatus.BLACK_WINS : GameStatus.WHITE_WINS;
        }
        return GameStatus.IN_PROGRESS;
    }

    private Set<PieceKind> getAllTypes(Board board) {
        return board.pieces().values().stream()
                .map(Piece::type)
                .collect(Collectors.toSet());
    }

    private Set<PieceKind> getTypesFor(Board board, Color color) {
        return board.pieces().values().stream()
                .filter(p -> p.isColor(color))
                .map(Piece::type)
                .collect(Collectors.toSet());
    }
}