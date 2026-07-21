package edu.austral.dissis.chess.model.pieces;

import edu.austral.dissis.common.model.PieceKind;

public final class King implements PieceKind {
    public static final King INSTANCE = new King();
    private King() {}

    @Override
    public String pieceId() {
        return "king";
    }
}
