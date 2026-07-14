package edu.austral.dissis.server;

import edu.austral.dissis.common.model.MoveResult;
import edu.austral.dissis.common.model.Position;
import edu.austral.dissis.common.network.payload.MovePayload;
import edu.austral.ingsis.clientserver.Message;
import edu.austral.ingsis.clientserver.MessageListener;

public class MoveMessageListener implements MessageListener<MovePayload> {

  private final GameServerState state;

  public MoveMessageListener(GameServerState state) {
    this.state = state;
  }

  @Override
  public void handleMessage(Message<MovePayload> message) {
    MovePayload payload = message.getPayload();
    Position from = new Position(payload.fromRow(), payload.fromCol());
    Position to = new Position(payload.toRow(), payload.toCol());
    MoveResult result = state.applyMove(from, to);
    if (result instanceof MoveResult.Success) {
      state.broadcastGameState();
    }
  }
}
