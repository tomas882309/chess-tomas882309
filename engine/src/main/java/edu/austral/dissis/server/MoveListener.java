package edu.austral.dissis.server;

import edu.austral.dissis.common.model.Position;
import edu.austral.dissis.server.payload.MovePayload;
import edu.austral.ingsis.clientserver.Message;
import edu.austral.ingsis.clientserver.MessageListener;

import java.util.function.BiConsumer;

public class MoveListener implements MessageListener<MovePayload> {

    private final BiConsumer<Position, Position> onMove;

    public MoveListener(BiConsumer<Position, Position> onMove) {
        this.onMove = onMove;
    }

    @Override
    public void handleMessage(Message<MovePayload> message) {
        MovePayload p = message.getPayload();
        onMove.accept(new Position(p.fromRow(), p.fromCol()), new Position(p.toRow(), p.toCol()));
    }
}
