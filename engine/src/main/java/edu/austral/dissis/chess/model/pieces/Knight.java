package edu.austral.dissis.chess.model.pieces;

import edu.austral.dissis.common.model.PieceKind;

public final class Knight implements PieceKind {
    public static final Knight INSTANCE = new Knight();
    private Knight() {}

    @Override
    public String pieceId() {
        return "knight";
    }
}
