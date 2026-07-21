package edu.austral.dissis.server;

import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Piece;
import edu.austral.dissis.common.model.Position;
import edu.austral.dissis.server.payload.GameStatePayload;
import edu.austral.dissis.server.payload.PiecePayload;

import java.util.List;

public class GameStateSerializer {

    public GameStatePayload serialize(GameState state) {
        List<PiecePayload> pieces = state.board().pieces().entrySet().stream()
                .map(e -> toPiecePayload(e.getKey(), e.getValue()))
                .toList();
        return new GameStatePayload(
                pieces,
                state.currentPlayer().name(),
                state.status().name(),
                state.board().rows(),
                state.board().cols()
        );
    }

    private PiecePayload toPiecePayload(Position pos, Piece piece) {
        return new PiecePayload(
                piece.id(),
                piece.type().pieceId(),
                piece.color().name(),
                pos.row(),
                pos.col()
        );
    }
}