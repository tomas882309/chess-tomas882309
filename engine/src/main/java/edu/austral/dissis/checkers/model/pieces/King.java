package edu.austral.dissis.checkers.model.pieces;

import edu.austral.dissis.common.model.PieceKind;

public class King implements PieceKind {
    public static final King INSTANCE = new King();

    private King() {}

    @Override
    public String pieceId() {
        return "king";
    }
}
