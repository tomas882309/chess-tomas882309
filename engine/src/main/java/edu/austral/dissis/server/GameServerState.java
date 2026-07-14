package edu.austral.dissis.server;

import edu.austral.dissis.checkers.model.CheckersPieceType;
import edu.austral.dissis.chess.model.PieceType;
import edu.austral.dissis.common.game.Game;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;
import edu.austral.dissis.common.model.MoveResult;
import edu.austral.dissis.common.model.Position;
import edu.austral.dissis.common.network.payload.GameStatePayload;
import edu.austral.ingsis.clientserver.Message;
import edu.austral.ingsis.clientserver.Server;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

public class GameServerState {

  private final Game game;
  private final BiFunction<Position, Position, Move> moveFactory;
  private final AtomicReference<Server> server = new AtomicReference<>();
  private Runnable uiUpdater = null;
  private String blackClientId = null;

  public GameServerState(Game game, BiFunction<Position, Position, Move> moveFactory) {
    this.game = game;
    this.moveFactory = moveFactory;
  }

  public void setServer(Server server) {
    this.server.set(server);
  }

  public void setUiUpdater(Runnable updater) {
    this.uiUpdater = updater;
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

  public void broadcastGameState() {
    Server s = server.get();
    if (s != null) {
      s.broadcast(new Message<>("game_state", buildPayload(game.currentState())));
    }
    if (uiUpdater != null) {
      uiUpdater.run();
    }
  }

  public GameStatePayload buildPayload(GameState state) {
    List<GameStatePayload.Piece> pieces = new ArrayList<>();
    state
        .board()
        .pieces()
        .forEach(
            (pos, piece) ->
                pieces.add(
                    new GameStatePayload.Piece(
                        toPieceId(piece.type()), piece.color().name(), pos.row(), pos.col())));
    return new GameStatePayload(
        pieces, state.currentPlayer().name(), state.status().name(), state.board().size());
  }

  private String toPieceId(Object type) {
    if (type instanceof PieceType pt) {
      return switch (pt) {
        case KING -> "king";
        case QUEEN -> "queen";
        case ROOK -> "rook";
        case BISHOP -> "bishop";
        case KNIGHT -> "knight";
        case PAWN -> "pawn";
      };
    } else if (type instanceof CheckersPieceType ct) {
      return switch (ct) {
        case MAN -> "man";
        case KING -> "king";
      };
    }
    return "pawn";
  }

  public synchronized String assignRole(String clientId) {
    if (blackClientId == null) {
      blackClientId = clientId;
      return "BLACK";
    }
    return "SPECTATOR";
  }

  public void sendRole(String clientId, String role) {
    Server s = server.get();
    if (s != null) {
      s.sendMessage(clientId, new Message<>("role", role));
    }
  }

  public void sendGameStateTo(String clientId) {
    Server s = server.get();
    if (s != null) {
      s.sendMessage(clientId, new Message<>("game_state", buildPayload(game.currentState())));
    }
  }
}
