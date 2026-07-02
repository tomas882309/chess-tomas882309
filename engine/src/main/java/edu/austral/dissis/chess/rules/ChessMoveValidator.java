package edu.austral.dissis.chess.rules;

import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Piece;
import edu.austral.dissis.common.rules.MoveValidator;
import java.util.Optional;

public class ChessMoveValidator implements MoveValidator {

  private final KingInCheckDetector checkDetector = new KingInCheckDetector();

  @Override
  public Optional<String> findViolation(Move move, GameState state) {
    var pieceOpt = state.board().pieceAt(move.from());
    if (pieceOpt.isEmpty()) {
      return Optional.of("No hay pieza en la posición de origen");
    }
    Piece piece = pieceOpt.get();
    if (!piece.isColor(state.currentPlayer())) {
      return Optional.of("No es tu turno");
    }
    if (!state.board().isWithinBounds(move.to())) {
      return Optional.of("Destino fuera del tablero");
    }
    if (!piece.moveStrategy().isValidMove(move, piece, state)) {
      return Optional.of("Movimiento inválido para esta pieza");
    }
    if (checkDetector.wouldLeaveKingInCheck(move, piece, state)) {
      return Optional.of("El movimiento deja al rey en jaque");
    }
    return Optional.empty();
  }
}
