package edu.austral.dissis.chess.model.pieces;

import edu.austral.dissis.common.model.PieceKind;

public class Archbishop implements PieceKind {
    public static final Archbishop INSTANCE = new Archbishop();
    private Archbishop() {}

    @Override
    public String pieceId() { return "archbishop"; }
}