package edu.austral.dissis.checkers.behaviour;

import edu.austral.dissis.common.behaviour.MoveBehaviour;
import edu.austral.dissis.common.model.*;

import java.util.ArrayList;
import java.util.List;

public class KingMoveBehaviour implements MoveBehaviour {

    @Override
    public boolean isValidMove(Move move, Piece piece, GameState state) {
        if (move.absRowDiff() != move.absColDiff() || move.absRowDiff() == 0) {
            return false;
        }
        return isSimpleMove(move, state.board()) || isCapture(move, piece, state.board());
    }

    private boolean isSimpleMove(Move move, Board board) {
        return pathBetween(move).stream().allMatch(p -> board.pieceAt(p).isEmpty())
                && board.pieceAt(move.to()).isEmpty();
    }

    private boolean isCapture(Move move, Piece piece, Board board) {
        if (board.pieceAt(move.to()).isPresent()) {
            return false;
        }
        List<Position> path = pathBetween(move);
        long opponents = path.stream()
                .filter(p -> board.pieceAt(p).filter(pc -> !pc.isColor(piece.color())).isPresent())
                .count();
        long friendlies = path.stream()
                .filter(p -> board.isOccupiedByColor(p, piece.color()))
                .count();
        return opponents == 1 && friendlies == 0;
    }

    private List<Position> pathBetween(Move move) {
        List<Position> path = new ArrayList<>();
        int rowStep = move.rowDiff() > 0 ? 1 : -1;
        int colStep = move.colDiff() > 0 ? 1 : -1;
        int row = move.from().row() + rowStep;
        int col = move.from().col() + colStep;
        while (row != move.to().row()) {
            path.add(new Position(row, col));
            row += rowStep;
            col += colStep;
        }
        return path;
    }
}