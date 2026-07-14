package edu.austral.dissis.chess.ui

import com.fasterxml.jackson.core.type.TypeReference
import edu.austral.dissis.chess.gui.CachedImageResolver
import edu.austral.dissis.chess.gui.DefaultImageResolver
import edu.austral.dissis.chess.gui.GameViewFactory
import edu.austral.dissis.common.network.payload.GameStatePayload
import edu.austral.dissis.server.ClientRole
import edu.austral.dissis.server.ServerConfig
import edu.austral.ingsis.clientserver.Client
import edu.austral.ingsis.clientserver.ClientConnectionListener
import edu.austral.ingsis.clientserver.Message
import edu.austral.ingsis.clientserver.MessageListener
import edu.austral.ingsis.clientserver.netty.client.NettyClientBuilder
import javafx.application.Application
import javafx.application.Application.launch
import javafx.application.Platform
import javafx.scene.Scene
import javafx.stage.Stage
import java.net.InetSocketAddress

fun main() {
    launch(ClientApplication::class.java)
}

class ClientApplication : Application() {
    private val imageResolver = CachedImageResolver(DefaultImageResolver())
    private var client: Client? = null

    override fun start(primaryStage: Stage) {
        primaryStage.title = "Chess - Cliente"
        val view = GameViewFactory.create(imageResolver)
        val engine = ClientGuiEngine()

        client =
            NettyClientBuilder.Companion
                .createDefault()
                .withAddress(InetSocketAddress(ServerConfig.HOST, ServerConfig.PORT))
                .withConnectionListener(
                    object : ClientConnectionListener {
                        override fun handleConnection() = Unit

                        override fun handleConnectionClosed() = Unit
                    },
                ).addMessageListener(
                    "game_state",
                    object : TypeReference<Message<GameStatePayload>>() {},
                    object : MessageListener<GameStatePayload> {
                        override fun handleMessage(message: Message<GameStatePayload>) {
                            println("game_state recibido: ${message.payload}")
                            val guiState = engine.onGameStateReceived(message.payload)
                            Platform.runLater { view.applyState(guiState) }
                        }
                    },
                ).addMessageListener(
                    "role",
                    object : TypeReference<Message<ClientRole>>() {},
                    object : MessageListener<ClientRole> {
                        override fun handleMessage(message: Message<ClientRole>) {
                            engine.setRole(message.payload)
                        }
                    },
                ).build()

        engine.setClient(client!!)
        client?.connect()

        view.inputEvents().subscribe { event -> view.applyState(engine.process(event)) }
        primaryStage.scene = Scene(view.pane())
        primaryStage.show()
    }
}
