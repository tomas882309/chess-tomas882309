package edu.austral.dissis.chess.behaviour;

import edu.austral.dissis.chess.model.EnPassantExtraState;
import edu.austral.dissis.common.model.*;
import edu.austral.dissis.common.behaviour.MoveBehaviour;

public class PawnMoveBehaviour implements MoveBehaviour {


    @Override
    public boolean isValidMove(Move move, Piece piece, GameState state) {
        int direction = piece.isColor(Color.WHITE) ? 1 : -1;
        return isStandardForward(move, direction, state.board())
                || isDoubleForward(move, direction, piece.color(), state.board())
                || isDiagonalCapture(move, direction, piece, state.board())
                || isEnPassant(move, direction, state);
    }

    private boolean isStandardForward(Move move, int direction, Board board){
        return move.rowDiff() == direction && move.colDiff() == 0 && board.pieceAt(move.to()).isEmpty();
    }

    private boolean isDoubleForward(Move move, int direction, Color color, Board board){
        if (move.rowDiff() != 2 * direction || move.colDiff() != 0){
            return false;
        }
        boolean onStartRow = color == Color.WHITE ? move.from().row() == 1 : move.from().row() == 6;
        Position middle = new Position(move.from().row() + direction, move.from().col());

        return onStartRow && board.pieceAt(middle).isEmpty() && board.pieceAt(move.to()).isEmpty();
    }

    private boolean isDiagonalCapture(Move move, int direction, Piece piece, Board board){
        return move.rowDiff() == direction && move.absColDiff() == 1 && board.pieceAt(move.to()).isPresent() && !board.isOccupiedByColor(move.to(), piece.color());
    }

    private boolean isEnPassant(Move move, int direction, GameState state){
        if (move.rowDiff() != direction || move.absColDiff() != 1){
            return false;
        }
        if(state.board().pieceAt(move.to()).isPresent()) {
            return false;
        }
        return state.extraState().get(EnPassantExtraState.class)
                .flatMap(EnPassantExtraState::target)
                .map(t -> t.equals(move.to()))
                .orElse(false);
    }
}
