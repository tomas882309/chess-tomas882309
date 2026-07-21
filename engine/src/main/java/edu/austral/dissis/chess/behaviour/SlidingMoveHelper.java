package edu.austral.dissis.chess.behaviour;

import edu.austral.dissis.common.model.Board;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Piece;
import edu.austral.dissis.common.model.Position;

public class SlidingMoveHelper {

    public boolean isPathClear (Move move, Board board) {
        int stepRow = Integer.signum(move.rowDiff());
        int stepCol = Integer.signum(move.colDiff());
        Position current = new Position(move.from().row() + stepRow, move.from().col() + stepCol);
        while(!current.equals(move.to())) {
            if (board.pieceAt(current).isPresent()){
                return false;
            }
            current = new Position(current.row() + stepRow, current.col() + stepCol);
        }
        return true;
    }

    public boolean isPathClearAndDestinationFree(Move move, Piece piece, Board board){
        return !board.isOccupiedByColor(move.to(), piece.color()) && isPathClear(move, board);
    }
}
