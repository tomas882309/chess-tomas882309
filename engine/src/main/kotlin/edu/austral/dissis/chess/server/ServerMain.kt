package edu.austral.dissis.chess.ui

import com.fasterxml.jackson.core.type.TypeReference
import edu.austral.dissis.chess.factory.CapablancaGameFactory
import edu.austral.dissis.chess.factory.ChessGameFactory
import edu.austral.dissis.chess.gui.CachedImageResolver
import edu.austral.dissis.chess.gui.DefaultImageResolver
import edu.austral.dissis.chess.gui.GameInputEvent
import edu.austral.dissis.chess.gui.GameViewFactory
import edu.austral.dissis.common.model.Color
import edu.austral.dissis.common.model.Move
import edu.austral.dissis.server.GameStateSerializer
import edu.austral.dissis.server.MoveListener
import edu.austral.dissis.server.ServerConfig
import edu.austral.dissis.server.payload.MovePayload
import edu.austral.ingsis.clientserver.Message
import edu.austral.ingsis.clientserver.Server
import edu.austral.ingsis.clientserver.ServerConnectionListener
import edu.austral.ingsis.clientserver.netty.server.NettyServerBuilder
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.stage.Stage

fun main() {
    Application.launch(ServerApplication::class.java)
}

class ServerApplication : Application() {
    private val imageResolver = CachedImageResolver(DefaultImageResolver())
    private val serializer = GameStateSerializer()
    private val adapter = GameAdapter(CapablancaGameFactory.createStandardGame())
    private var server: Server? = null
    private var blackClientId: String? = null

    override fun start(stage: Stage) {
        stage.title = "Chess - Servidor (Blancas)"
        val view = GameViewFactory.create(imageResolver)

        server = NettyServerBuilder.Companion.createDefault()
            .withPort(ServerConfig.PORT)
            .withConnectionListener(object : ServerConnectionListener {
                override fun handleClientConnection(clientId: String) {
                    val role = assignRole(clientId)
                    server?.sendMessage(clientId, Message("role", role))
                    server?.sendMessage(clientId, Message("game_state", serializer.serialize(adapter.currentGame().current())))
                }
                override fun handleClientConnectionClosed(clientId: String) = Unit
            })
            .addMessageListener(
                "move",
                object : TypeReference<Message<MovePayload>>() {},
                MoveListener { from, to ->
                    if (adapter.currentGame().current().currentPlayer() == Color.BLACK) {
                        val guiState = adapter.applyNetworkMove(Move(from, to))
                        if (guiState != null) {
                            broadcast()
                            Platform.runLater { view.applyState(guiState) }
                        }
                    }
                }
            )
            .build()

        server?.start()

        adapter.process(
            view.inputEvents().map { event ->
                if (event is GameInputEvent.MoveEvent && adapter.currentGame().current().currentPlayer() != Color.WHITE)
                    GameInputEvent.Init
                else
                    event
            }
        ).subscribe { guiState ->
            broadcast()
            Platform.runLater { view.applyState(guiState) }
        }

        stage.scene = Scene(view.pane())
        stage.show()
    }

    private fun broadcast() {
        server?.broadcast(Message("game_state", serializer.serialize(adapter.currentGame().current())))
    }

    private fun assignRole(clientId: String): String {
        if (blackClientId == null) {
            blackClientId = clientId
            return "BLACK"
        }
        return "SPECTATOR"
    }
}
