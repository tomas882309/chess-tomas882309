package edu.austral.dissis.chess.factory;

import edu.austral.dissis.chess.behaviour.*;
import edu.austral.dissis.chess.model.pieces.*;
import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.Piece;

public class ChessPieceFactory {
    private static int counter = 0;

    private static String nextId() { return String.valueOf(++counter); }

    public static Piece king(Color color)   {
        return new Piece(nextId(), color, King.INSTANCE,   new KingMoveBehaviour());
    }

    public static Piece queen(Color color)  {
        return new Piece(nextId(), color, Queen.INSTANCE,  new QueenMoveBehaviour());
    }

    public static Piece rook(Color color)   {
        return new Piece(nextId(), color, Rook.INSTANCE,   new RookMoveBehaviour());
    }

    public static Piece bishop(Color color) {
        return new Piece(nextId(), color, Bishop.INSTANCE, new BishopMoveBehaviour());
    }

    public static Piece knight(Color color) {
        return new Piece(nextId(), color, Knight.INSTANCE, new KnightMoveBehaviour());
    }

    public static Piece pawn(Color color)   {
        return new Piece(nextId(), color, Pawn.INSTANCE,   new PawnMoveBehaviour());
    }

}