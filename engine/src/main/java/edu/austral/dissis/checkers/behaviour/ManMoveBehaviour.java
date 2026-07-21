package edu.austral.dissis.checkers.behaviour;

import edu.austral.dissis.common.behaviour.MoveBehaviour;
import edu.austral.dissis.common.model.*;

public class ManMoveBehaviour implements MoveBehaviour {

    @Override
    public boolean isValidMove(Move move, Piece piece, GameState state) {
        int direction = piece.isColor(Color.WHITE) ? -1 : 1;
        return isSimpleMove(move, state.board(), direction)
                || isCapture(move,piece, state.board(), direction);
    }

    private boolean isSimpleMove(Move move, Board board, int direction){
        return move.rowDiff() == direction && move.absColDiff() == 1 && board.pieceAt(move.to()).isEmpty();
    }

    private boolean isCapture(Move move, Piece piece, Board board, int direction){
        if (move.rowDiff() != 2 * direction || move.absColDiff() != 2){
            return false;
        }
        Position middle = middlePosition(move);
        return board.pieceAt(middle).isPresent()
                && !board.isOccupiedByColor(middle, piece.color())
                && board.pieceAt(move.to()).isEmpty();
    }

    private Position middlePosition(Move move){
        return new Position((move.from().row() + move.to().row()) / 2,
                (move.from().col() + move.to().col()) / 2);
    }
}
