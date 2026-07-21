package edu.austral.dissis.chess.model.pieces;

import edu.austral.dissis.common.model.PieceKind;

public final class Rook implements PieceKind {
    public static final Rook INSTANCE = new Rook();
    private Rook() {}

    @Override
    public String pieceId() {
        return "rook";
    }
}
