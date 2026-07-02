package edu.austral.dissis.checkers.rules;

import edu.austral.dissis.checkers.model.CheckersMove;
import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.GameStatus;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Position;
import edu.austral.dissis.common.rules.WinCondition;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CheckersWinCondition implements WinCondition {

  private final CheckersMoveValidator moveValidator = new CheckersMoveValidator();

  @Override
  public GameStatus evaluate(GameState state, Move lastMove) {
    Color player = state.currentPlayer();
    if (!hasAnyPiece(player, state) || !hasAnyLegalMove(player, state)) {
      return player == Color.WHITE ? GameStatus.BLACK_WINS : GameStatus.WHITE_WINS;
    }
    return GameStatus.IN_PROGRESS;
  }

  private boolean hasAnyPiece(Color color, GameState state) {
    return state.board().pieces().values().stream().anyMatch(p -> p.isColor(color));
  }

  private boolean hasAnyLegalMove(Color color, GameState state) {
    return state.board().pieces().entrySet().stream()
        .filter(e -> e.getValue().isColor(color))
        .anyMatch(e -> pieceHasLegalMove(e.getKey(), state));
  }

  private boolean pieceHasLegalMove(Position from, GameState state) {
    return allPositions(state.board().size())
        .anyMatch(to -> moveValidator.findViolation(new CheckersMove(from, to), state).isEmpty());
  }

  private Stream<Position> allPositions(int size) {
    return IntStream.range(0, size)
        .boxed()
        .flatMap(r -> IntStream.range(0, size).mapToObj(c -> new Position(r, c)));
  }
}
