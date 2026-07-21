package edu.austral.dissis.chess.factory;


import edu.austral.dissis.chess.behaviour.ArchbishopMoveBehaviour;
import edu.austral.dissis.chess.behaviour.ChancellorMoveBehaviour;
import edu.austral.dissis.chess.model.pieces.Archbishop;
import edu.austral.dissis.chess.model.pieces.Chancellor;
import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.Piece;

public class CapablancaPieceFactory {
    private static int counter = 0;
    private static String nextId() { return "cap" + (++counter); }

    public static Piece archbishop(Color color) {
        return new Piece(nextId(), color, Archbishop.INSTANCE, new ArchbishopMoveBehaviour());
    }

    public static Piece chancellor(Color color) {
        return new Piece(nextId(), color, Chancellor.INSTANCE, new ChancellorMoveBehaviour());
    }
}