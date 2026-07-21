package edu.austral.dissis.checkers.rules;

import edu.austral.dissis.checkers.model.pieces.Man;
import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Piece;
import edu.austral.dissis.common.model.Position;

public class CaptureChecker {

    public static boolean canCaptureFrom(Position from, GameState state) {
        return state.board().pieceAt(from)
                .map(piece -> hasCapture(from, piece, state))
                .orElse(false);
    }

    public static boolean anyCaptureAvailable(Color color, GameState state) {
        return state.board().pieces().entrySet().stream()
                .filter(e -> e.getValue().isColor(color))
                .anyMatch(e -> hasCapture(e.getKey(), e.getValue(), state));
    }

    private static boolean hasCapture(Position from, Piece piece, GameState state) {
        if (piece.isType(Man.INSTANCE)) return hasManCapture(from, piece, state);
        return hasKingCapture(from, piece, state);
    }

    private static boolean hasManCapture(Position from, Piece piece, GameState state) {
        int direction = piece.isColor(Color.WHITE) ? -1 : 1;
        for (int dc : new int[]{-1, 1}) {
            Position middle = new Position(from.row() + direction, from.col() + dc);
            Position to = new Position(from.row() + direction * 2, from.col() + dc * 2);
            if (!state.board().isWithinBounds(to)) continue;
            if (state.board().pieceAt(middle).filter(p -> !p.isColor(piece.color())).isPresent()
                    && state.board().pieceAt(to).isEmpty()) return true;
        }
        return false;
    }

    private static boolean hasKingCapture(Position from, Piece piece, GameState state) {
        for (int dr : new int[]{-1, 1})
            for (int dc : new int[]{-1, 1})
                if (hasCaptureInDirection(from, piece, state, dr, dc)) return true;
        return false;
    }

    private static boolean hasCaptureInDirection(Position from, Piece piece, GameState state, int dr, int dc) {
        boolean foundOpponent = false;
        int row = from.row() + dr;
        int col = from.col() + dc;
        while (state.board().isWithinBounds(new Position(row, col))) {
            Position pos = new Position(row, col);
            if (state.board().isOccupiedByColor(pos, piece.color())) break;
            if (state.board().pieceAt(pos).isPresent()) {
                if (foundOpponent) break;
                foundOpponent = true;
            } else if (foundOpponent) return true;
            row += dr;
            col += dc;
        }
        return false;
    }
}