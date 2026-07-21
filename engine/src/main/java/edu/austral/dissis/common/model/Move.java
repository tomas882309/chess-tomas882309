package edu.austral.dissis.common.model;

public record Move(Position from, Position to) {

    public int rowDiff() {
        return to.row() - from.row();
    }

    public int colDiff() {
        return to.col() - from.col();
    }

    public int absRowDiff() {
        return Math.abs(rowDiff());
    }

    public int absColDiff() {
        return Math.abs(colDiff());
    }

}
