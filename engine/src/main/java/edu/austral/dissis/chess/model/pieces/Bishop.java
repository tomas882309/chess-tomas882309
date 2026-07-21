package edu.austral.dissis.chess.model.pieces;

import edu.austral.dissis.common.model.PieceKind;

public final class Bishop implements PieceKind {
    public static final Bishop INSTANCE = new Bishop();
    private Bishop() {}

    @Override
    public String pieceId() {
        return "bishop";
    }
}
