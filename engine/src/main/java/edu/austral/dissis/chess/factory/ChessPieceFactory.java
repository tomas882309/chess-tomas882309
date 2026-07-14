package edu.austral.dissis.chess.factory;

import edu.austral.dissis.chess.model.pieces.Bishop;
import edu.austral.dissis.chess.model.pieces.King;
import edu.austral.dissis.chess.model.pieces.Knight;
import edu.austral.dissis.chess.model.pieces.Pawn;
import edu.austral.dissis.chess.model.pieces.Queen;
import edu.austral.dissis.chess.model.pieces.Rook;
import edu.austral.dissis.chess.strategy.BishopMoveStrategy;
import edu.austral.dissis.chess.strategy.KingMoveStrategy;
import edu.austral.dissis.chess.strategy.KnightMoveStrategy;
import edu.austral.dissis.chess.strategy.PawnMoveStrategy;
import edu.austral.dissis.chess.strategy.QueenMoveStrategy;
import edu.austral.dissis.chess.strategy.RookMoveStrategy;
import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.Piece;

public class ChessPieceFactory {

  public static Piece king(Color color) {
    return new Piece(color, King.INSTANCE, new KingMoveStrategy());
  }

  public static Piece queen(Color color) {
    return new Piece(color, Queen.INSTANCE, new QueenMoveStrategy());
  }

  public static Piece rook(Color color) {
    return new Piece(color, Rook.INSTANCE, new RookMoveStrategy());
  }

  public static Piece bishop(Color color) {
    return new Piece(color, Bishop.INSTANCE, new BishopMoveStrategy());
  }

  public static Piece knight(Color color) {
    return new Piece(color, Knight.INSTANCE, new KnightMoveStrategy());
  }

  public static Piece pawn(Color color) {
    return new Piece(color, Pawn.INSTANCE, new PawnMoveStrategy());
  }
}
