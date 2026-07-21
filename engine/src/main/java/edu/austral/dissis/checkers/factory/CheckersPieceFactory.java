package edu.austral.dissis.checkers.factory;

import edu.austral.dissis.checkers.behaviour.KingMoveBehaviour;
import edu.austral.dissis.checkers.behaviour.ManMoveBehaviour;
import edu.austral.dissis.checkers.model.pieces.King;
import edu.austral.dissis.checkers.model.pieces.Man;
import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.Piece;

public class CheckersPieceFactory {
    private static int counter = 0;

    private static String nextId() { return "c" + (++counter); }

    public static Piece man(Color color) {
        return new Piece(nextId(), color, Man.INSTANCE, new ManMoveBehaviour());
    }

    public static Piece king(Color color) {
        return new Piece(nextId(), color, King.INSTANCE, new KingMoveBehaviour());
    }
}