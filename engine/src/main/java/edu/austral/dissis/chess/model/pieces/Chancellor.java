package edu.austral.dissis.chess.model.pieces;

import edu.austral.dissis.common.model.PieceKind;

public class Chancellor implements PieceKind {
    public static final Chancellor INSTANCE = new Chancellor();
    private Chancellor() {}

    @Override
    public String pieceId() { return "chancellor"; }
}