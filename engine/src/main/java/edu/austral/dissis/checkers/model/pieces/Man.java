package edu.austral.dissis.checkers.model.pieces;

import edu.austral.dissis.common.model.PieceKind;

public class Man implements PieceKind {
    public static final Man INSTANCE = new Man();

    private Man() {}

    @Override
    public String pieceId() {
        return "man";
    }
}
