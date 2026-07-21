package edu.austral.dissis.server;

import edu.austral.dissis.server.payload.GameStatePayload;
import edu.austral.ingsis.clientserver.Message;
import edu.austral.ingsis.clientserver.MessageListener;

import java.util.function.Consumer;

public class GameStateListener implements MessageListener<GameStatePayload> {

    private final Consumer<GameStatePayload> onState;

    public GameStateListener(Consumer<GameStatePayload> onState) {
        this.onState = onState;
    }

    @Override
    public void handleMessage(Message<GameStatePayload> message) {
        onState.accept(message.getPayload());
    }
}