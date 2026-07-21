package edu.austral.dissis.chess.model.pieces;

import edu.austral.dissis.common.model.PieceKind;

public final class Pawn implements PieceKind {
    public static final Pawn INSTANCE = new Pawn();
    private Pawn() {}

    @Override
    public String pieceId() {
        return "pawn";
    }
}
