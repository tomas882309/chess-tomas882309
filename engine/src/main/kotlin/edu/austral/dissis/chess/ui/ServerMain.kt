package edu.austral.dissis.chess.ui

import com.fasterxml.jackson.core.type.TypeReference
import edu.austral.dissis.chess.factory.ChessGameFactory
import edu.austral.dissis.chess.gui.CachedImageResolver
import edu.austral.dissis.chess.gui.DefaultImageResolver
import edu.austral.dissis.chess.gui.GameViewFactory
import edu.austral.dissis.chess.model.ChessMove
import edu.austral.dissis.common.network.payload.InitPayload
import edu.austral.dissis.common.network.payload.MovePayload
import edu.austral.dissis.server.GameServerState
import edu.austral.dissis.server.MoveMessageListener
import edu.austral.dissis.server.ServerConfig
import edu.austral.ingsis.clientserver.Message
import edu.austral.ingsis.clientserver.MessageListener
import edu.austral.ingsis.clientserver.ServerConnectionListener
import edu.austral.ingsis.clientserver.netty.server.NettyServerBuilder
import javafx.application.Application
import javafx.application.Application.launch
import javafx.application.Platform
import javafx.scene.Scene
import javafx.stage.Stage

fun main() {
    launch(ServerApplication::class.java)
}

class ServerApplication : Application() {
    private val imageResolver = CachedImageResolver(DefaultImageResolver())

    override fun start(primaryStage: Stage) {
        val serverState =
            GameServerState(ChessGameFactory.createStandardGame()) { from, to ->
                ChessMove.standard(from, to)
            }

        primaryStage.title = "Chess - Servidor (Blancas)"
        val view = GameViewFactory.create(imageResolver)
        val engine = ServerGuiEngine(serverState)

        serverState.setUiUpdater {
            engine.rebuildIds()
            Platform.runLater { view.applyState(engine.buildCurrentGuiState()) }
        }

        val server =
            NettyServerBuilder.Companion
                .createDefault()
                .withPort(ServerConfig.PORT)
                .withConnectionListener(
                    object : ServerConnectionListener {
                        override fun handleClientConnection(clientId: String) {
                            val role = serverState.assignRole(clientId)
                            serverState.sendRole(clientId, role)
                            serverState.sendGameStateTo(clientId)
                        }

                        override fun handleClientConnectionClosed(clientId: String) = Unit
                    },
                ).addMessageListener(
                    "move",
                    object : TypeReference<Message<MovePayload>>() {},
                    MoveMessageListener(serverState),
                ).build()
        serverState.setServer(server)
        server.start()

        view.inputEvents().subscribe { event -> view.applyState(engine.process(event)) }
        primaryStage.scene = Scene(view.pane())
        primaryStage.show()
    }
}
