package edu.austral.dissis.common.network.payload;

import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Piece;
import edu.austral.dissis.common.model.Position;
import java.util.List;

public class GameStatePayloadBuilder {

  public GameStatePayload build(GameState state) {
    List<GameStatePayload.Piece> pieces =
        state.board().pieces().entrySet().stream()
            .map(e -> toPiece(e.getKey(), e.getValue()))
            .toList();
    return new GameStatePayload(
        pieces, state.currentPlayer().name(), state.status().name(), state.board().size());
  }

  private GameStatePayload.Piece toPiece(Position pos, Piece piece) {
    return new GameStatePayload.Piece(
        piece.type().pieceId(), piece.color().name(), pos.row(), pos.col());
  }
}
