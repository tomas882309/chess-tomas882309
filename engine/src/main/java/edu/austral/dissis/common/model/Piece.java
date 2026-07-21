package edu.austral.dissis.common.model;

import edu.austral.dissis.common.behaviour.MoveBehaviour;

public record Piece(String id, Color color, PieceKind type, MoveBehaviour moveBehaviour) {

    public boolean isColor (Color expected) {
        return color == expected;
    }

    public boolean isType (PieceKind expected){
        return type.equals(expected);
    }
}
