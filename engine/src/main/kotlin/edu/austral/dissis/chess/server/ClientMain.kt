package edu.austral.dissis.chess.ui

import com.fasterxml.jackson.core.type.TypeReference
import edu.austral.dissis.chess.gui.BoardSize
import edu.austral.dissis.chess.gui.CachedImageResolver
import edu.austral.dissis.chess.gui.ChessPiece
import edu.austral.dissis.chess.gui.DefaultImageResolver
import edu.austral.dissis.chess.gui.GameInputEvent
import edu.austral.dissis.chess.gui.GameViewFactory
import edu.austral.dissis.chess.gui.PlayerColor
import edu.austral.dissis.common.model.GameStatus
import edu.austral.dissis.server.GameStateListener
import edu.austral.dissis.server.ServerConfig
import edu.austral.dissis.server.payload.GameStatePayload
import edu.austral.dissis.server.payload.MovePayload
import edu.austral.ingsis.clientserver.Client
import edu.austral.ingsis.clientserver.ClientConnectionListener
import edu.austral.ingsis.clientserver.Message
import edu.austral.ingsis.clientserver.MessageListener
import edu.austral.ingsis.clientserver.netty.client.NettyClientBuilder
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.stage.Stage
import java.net.InetSocketAddress
import edu.austral.dissis.chess.gui.GameState as GuiGameState
import edu.austral.dissis.chess.gui.Position as GuiPosition

fun main() {
    Application.launch(ClientApplication::class.java)
}

class ClientApplication : Application() {
    private val imageResolver = CachedImageResolver(DefaultImageResolver())
    private var client: Client? = null
    private var role: String = "SPECTATOR"
    private var currentGuiState: GuiGameState = GuiGameState.Playing(
        boardSize = BoardSize(8, 8),
        pieces = emptyList(),
        currentPlayer = PlayerColor.WHITE,
        canUndo = false,
        canRedo = false
    )

    override fun start(stage: Stage) {
        stage.title = "Chess - Cliente"
        val view = GameViewFactory.create(imageResolver)

        client = NettyClientBuilder.Companion.createDefault()
            .withAddress(InetSocketAddress(ServerConfig.HOST, ServerConfig.PORT))
            .withConnectionListener(object : ClientConnectionListener {
                override fun handleConnection() = Unit
                override fun handleConnectionClosed() = Unit
            })
            .addMessageListener(
                "role",
                object : TypeReference<Message<String>>() {},
                object : MessageListener<String> {
                    override fun handleMessage(message: Message<String>) {
                        role = message.payload
                    }
                }
            )
            .addMessageListener(
                "game_state",
                object : TypeReference<Message<GameStatePayload>>() {},
                GameStateListener { payload ->
                    val guiState = toGuiState(payload)
                    currentGuiState = guiState
                    Platform.runLater { view.applyState(guiState) }
                }
            )
            .build()

        client?.connect()

        view.inputEvents().subscribe { event ->
            when (event) {
                is GameInputEvent.Init -> view.applyState(currentGuiState)
                is GameInputEvent.MoveEvent -> {
                    val state = currentGuiState
                    if (role == "BLACK"
                        && state is GuiGameState.Playing
                        && state.currentPlayer == PlayerColor.BLACK) {
                        client?.send(Message("move", MovePayload(
                            event.move.from.row - 1,
                            event.move.from.column - 1,
                            event.move.to.row - 1,
                            event.move.to.column - 1
                        )))
                    }
                }
                else -> Unit
            }
        }

        stage.scene = Scene(view.pane())
        stage.show()
    }

    private fun toGuiState(payload: GameStatePayload): GuiGameState {
        if (payload.status == GameStatus.WHITE_WINS.name) return GuiGameState.GameOver(PlayerColor.WHITE)
        if (payload.status == GameStatus.BLACK_WINS.name) return GuiGameState.GameOver(PlayerColor.BLACK)
        val pieces = payload.pieces.map { p ->
            ChessPiece(
                id = p.id,
                color = if (p.color == "WHITE") PlayerColor.WHITE else PlayerColor.BLACK,
                position = GuiPosition(p.row + 1, p.col + 1),
                pieceId = p.pieceId
            )
        }
        return GuiGameState.Playing(
            boardSize = BoardSize(payload.boardCols, payload.boardRows),
            pieces = pieces,
            currentPlayer = if (payload.currentPlayer == "WHITE") PlayerColor.WHITE else PlayerColor.BLACK,
            canUndo = false,
            canRedo = false
        )
    }
}
