package edu.austral.dissis.chess.rules;

import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.GameStatus;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.Piece;
import edu.austral.dissis.common.model.PieceKind;
import edu.austral.dissis.common.rules.WinCondition;
import java.util.Set;
import java.util.stream.Collectors;

public class ExtinctionWinCondition implements WinCondition {

  private final Set<PieceKind> requiredTypes;

  public ExtinctionWinCondition(Set<PieceKind> requiredTypes) {
    this.requiredTypes = requiredTypes;
  }

  @Override
  public GameStatus evaluate(GameState state, Move lastMove) {
    Color nextPlayer = state.currentPlayer();
    if (anyTypeExtinct(nextPlayer, state)) {
      return nextPlayer == Color.WHITE ? GameStatus.BLACK_WINS : GameStatus.WHITE_WINS;
    }
    return GameStatus.IN_PROGRESS;
  }

  private boolean anyTypeExtinct(Color color, GameState state) {
    Set<PieceKind> present =
        state.board().pieces().values().stream()
            .filter(p -> p.isColor(color))
            .map(Piece::type)
            .collect(Collectors.toSet());
    return requiredTypes.stream().anyMatch(t -> !present.contains(t));
  }
}
