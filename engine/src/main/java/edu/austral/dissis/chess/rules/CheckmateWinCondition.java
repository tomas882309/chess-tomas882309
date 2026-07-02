package edu.austral.dissis.chess.rules;

import edu.austral.dissis.chess.model.ChessMove;
import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.GameStatus;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Position;
import edu.austral.dissis.common.rules.WinCondition;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CheckmateWinCondition implements WinCondition {

  private final KingInCheckDetector checkDetector = new KingInCheckDetector();
  private final ChessMoveValidator moveValidator = new ChessMoveValidator();

  @Override
  public GameStatus evaluate(GameState state, Move lastMove) {
    Color player = state.currentPlayer();
    boolean inCheck = checkDetector.isKingInCheck(player, state);
    boolean hasLegalMoves = hasAnyLegalMove(player, state);
    if (!hasLegalMoves && inCheck) {
      return player == Color.WHITE ? GameStatus.BLACK_WINS : GameStatus.WHITE_WINS;
    }
    if (!hasLegalMoves) {
      return GameStatus.DRAW;
    }
    return GameStatus.IN_PROGRESS;
  }

  private boolean hasAnyLegalMove(Color color, GameState state) {
    return state.board().pieces().entrySet().stream()
        .filter(e -> e.getValue().isColor(color))
        .anyMatch(e -> pieceHasLegalMove(e.getKey(), state));
  }

  private boolean pieceHasLegalMove(Position from, GameState state) {
    return allPositions(state.board().size())
        .anyMatch(to -> moveValidator.findViolation(ChessMove.standard(from, to), state).isEmpty());
  }

  private Stream<Position> allPositions(int size) {
    return IntStream.range(0, size)
        .boxed()
        .flatMap(r -> IntStream.range(0, size).mapToObj(c -> new Position(r, c)));
  }
}
