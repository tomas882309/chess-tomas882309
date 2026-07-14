package edu.austral.dissis.server;

import edu.austral.dissis.common.game.Game;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.MoveResult;
import edu.austral.dissis.common.model.Position;
import edu.austral.dissis.common.network.payload.GameStatePayloadBuilder;
import edu.austral.ingsis.clientserver.Message;
import edu.austral.ingsis.clientserver.Server;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

public class GameServerState {

  private final Game game;
  private final BiFunction<Position, Position, Move> moveFactory;
  private final GameStatePayloadBuilder payloadBuilder = new GameStatePayloadBuilder();
  private final AtomicReference<Server> server = new AtomicReference<>();
  private Optional<Runnable> uiUpdater = Optional.empty();
  private Optional<String> blackClientId = Optional.empty();

  public GameServerState(Game game, BiFunction<Position, Position, Move> moveFactory) {
    this.game = game;
    this.moveFactory = moveFactory;
  }

  public void setServer(Server server) {
    this.server.set(server);
  }

  public void setUiUpdater(Runnable updater) {
    this.uiUpdater = Optional.of(updater);
  }

  public synchronized GameState currentGameState() {
    return game.currentState();
  }

  public synchronized MoveResult applyMove(Position from, Position to) {
    return game.executeMove(moveFactory.apply(from, to));
  }

  public synchronized MoveResult undo() {
    return game.undo();
  }

  public synchronized MoveResult redo() {
    return game.redo();
  }

  public synchronized boolean canUndo() {
    return game.canUndo();
  }

  public synchronized boolean canRedo() {
    return game.canRedo();
  }

  public synchronized ClientRole assignRole(String clientId) {
    if (blackClientId.isEmpty()) {
      blackClientId = Optional.of(clientId);
      return ClientRole.BLACK;
    }
    return ClientRole.SPECTATOR;
  }

  public void sendRole(String clientId, ClientRole role) {
    Optional.ofNullable(server.get())
        .ifPresent(s -> s.sendMessage(clientId, new Message<>("role", role)));
  }

  public void broadcastGameState() {
    Optional.ofNullable(server.get())
        .ifPresent(
            s ->
                s.broadcast(
                    new Message<>("game_state", payloadBuilder.build(game.currentState()))));
    uiUpdater.ifPresent(Runnable::run);
  }

  public void sendGameStateTo(String clientId) {
    Optional.ofNullable(server.get())
        .ifPresent(
            s ->
                s.sendMessage(
                    clientId,
                    new Message<>("game_state", payloadBuilder.build(game.currentState()))));
  }
}
