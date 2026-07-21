package edu.austral.dissis.chess.rules.move;

import edu.austral.dissis.chess.model.pieces.King;
import edu.austral.dissis.common.model.Board;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Position;
import edu.austral.dissis.common.rules.move.MoveValidator;

import java.util.Optional;

public class CastlingValidator implements MoveValidator {
    private final KingInCheckDetector detector = new KingInCheckDetector();

    @Override
    public Optional<String> findViolation(Move move, GameState state) {
        if (!isCastlingAttempt(move, state)) {
            return Optional.empty();
        }
        if (!isPathClear(move, state)){
            return Optional.of("El camino para enrocar no está libre");
        }
        if (detector.isKingInCheck(state.currentPlayer(), state)){
            return Optional.of("No podés enrocar estando en jaque");
        }
        if (passesThroughCheck(move, state)){
            return Optional.of("El rey pasa por una casilla amenazada");
        }
        return Optional.empty();
    }

    private boolean isCastlingAttempt(Move move, GameState state) {
        return state.board().pieceAt(move.from())
                .filter(p -> p.isType(King.INSTANCE) && p.isColor(state.currentPlayer()))
                .isPresent()
                && move.absColDiff() == 2 && move.absRowDiff() == 0;
    }

    private boolean isPathClear(Move move, GameState state) {
        int row = move.from().row();
        int rookCol = move.colDiff() > 0 ? 7 : 0;
        int startCol = Math.min(move.from().col(), rookCol) + 1;
        int endCol = Math.max(move.from().col(), rookCol);
        for (int col = startCol; col < endCol; col++)
            if (state.board().pieceAt(new Position(row, col)).isPresent()){
                return false;
            }
        return true;
    }

    private boolean passesThroughCheck(Move move, GameState state) {
        int step = move.colDiff() > 0 ? 1 : -1;
        Position intermediate = new Position(move.from().row(), move.from().col() + step);
        Board intermediateBoard = state.board().boardWithMove(move.from(), intermediate);
        GameState intermediateState = new GameState(intermediateBoard, state.currentPlayer(), state.status(), state.extraState());
        return detector.isKingInCheck(state.currentPlayer(), intermediateState);
    }
}
