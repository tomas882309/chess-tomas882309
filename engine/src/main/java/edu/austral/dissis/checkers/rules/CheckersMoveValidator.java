package edu.austral.dissis.checkers.rules;

import edu.austral.dissis.checkers.model.CheckersExtra;
import edu.austral.dissis.checkers.model.CheckersMove;
import edu.austral.dissis.common.model.Board;
import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Piece;
import edu.austral.dissis.common.model.Position;
import edu.austral.dissis.common.rules.MoveValidator;
import java.util.Optional;
import java.util.stream.IntStream;

public class CheckersMoveValidator implements MoveValidator {

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
    if (isMultiJumpViolation(move, state)) {
      return Optional.of("Debés continuar el multi-salto con la misma pieza");
    }
    if (!piece.moveStrategy().isValidMove(move, piece, state)) {
      return Optional.of("Movimiento inválido para esta pieza");
    }
    if (!isCapture(move, state.board()) && hasMandatoryCapture(state.currentPlayer(), state)) {
      return Optional.of("Hay una captura obligatoria disponible");
    }
    return Optional.empty();
  }

  private boolean isMultiJumpViolation(Move move, GameState state) {
    if (!(state.extra() instanceof CheckersExtra extra) || !extra.isMultiJump()) {
      return false;
    }
    return extra.multiJumpPosition().map(pos -> !pos.equals(move.from())).orElse(false);
  }

  public boolean isCapture(Move move, Board board) {
    int dr = move.to().row() - move.from().row();
    int dc = move.to().col() - move.from().col();
    if (Math.abs(dr) != Math.abs(dc) || Math.abs(dr) < 2) {
      return false;
    }
    int stepR = Integer.signum(dr);
    int stepC = Integer.signum(dc);
    int dist = Math.abs(dr);
    for (int i = 1; i < dist; i++) {
      Position cur = move.from().offset(i * stepR, i * stepC);
      if (board.pieceAt(cur).isPresent()) {
        return true;
      }
    }
    return false;
  }

  public boolean hasMandatoryCapture(Color color, GameState state) {
    return state.board().pieces().entrySet().stream()
        .filter(e -> e.getValue().isColor(color))
        .anyMatch(e -> pieceCanCapture(e.getKey(), e.getValue(), state));
  }

  public boolean pieceCanCapture(Position from, Piece piece, GameState state) {
    int size = state.board().size();
    return IntStream.rangeClosed(2, size - 1)
        .boxed()
        .flatMap(
            dist ->
                java.util.Arrays.stream(
                        new int[][] {{dist, dist}, {dist, -dist}, {-dist, dist}, {-dist, -dist}})
                    .map(d -> from.offset(d[0], d[1])))
        .filter(to -> state.board().isWithinBounds(to))
        .anyMatch(
            to -> {
              CheckersMove candidate = new CheckersMove(from, to);
              return piece.moveStrategy().isValidMove(candidate, piece, state)
                  && isCapture(candidate, state.board());
            });
  }
}
