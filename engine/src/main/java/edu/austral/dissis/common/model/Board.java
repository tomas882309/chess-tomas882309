package edu.austral.dissis.common.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record Board(Map<Position, Piece> pieces, int rows, int cols) {

    public Optional<Piece> pieceAt(Position pos) {
        return Optional.ofNullable(pieces.get(pos));
    }

    public boolean isOccupiedByColor(Position pos, Color color) {
        return pieceAt(pos).map(piece -> piece.isColor(color)).orElse(false);
    }

    public boolean isWithinBounds(Position pos){
        return pos.row() >= 0 && pos.col() >= 0 && pos.row() < rows && pos.col() < cols;
    }

    public Board boardWithMove(Position from, Position to) {
        var updated = new HashMap<>(pieces);
        updated.put(to, updated.remove(from));
        return new Board(updated, rows, cols);
    }


    public Board withoutPieceAt(Position pos) {
        var updated = new HashMap<>(pieces);
        updated.remove(pos);
        return new Board(updated, rows, cols);
    }

    public Board withPieceAt(Position pos, Piece piece) {
        var updated = new HashMap<>(pieces);
        updated.put(pos, piece);
        return new Board(updated, rows, cols);
    }

}
