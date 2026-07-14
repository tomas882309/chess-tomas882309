package edu.austral.dissis.chess.ui

import edu.austral.dissis.chess.gui.BoardSize
import edu.austral.dissis.chess.gui.ChessPiece
import edu.austral.dissis.chess.gui.GameInputEvent
import edu.austral.dissis.chess.gui.PlayerColor
import edu.austral.dissis.common.model.GameStatus
import edu.austral.dissis.common.network.payload.GameStatePayload
import edu.austral.dissis.common.network.payload.MovePayload
import edu.austral.dissis.server.ClientRole
import edu.austral.ingsis.clientserver.Client
import edu.austral.ingsis.clientserver.Message
import edu.austral.dissis.chess.gui.GameState as GuiGameState
import edu.austral.dissis.chess.gui.Move as GuiMove
import edu.austral.dissis.chess.gui.Position as GuiPosition

class ClientGuiEngine {
    private var client: Client? = null
    private var idCounter = 0
    private val posToId: MutableMap<String, String> = mutableMapOf()
    private var role: ClientRole = ClientRole.SPECTATOR
    private var currentGuiState: GuiGameState =
        GuiGameState.Playing(
            boardSize = BoardSize(8, 8),
            pieces = emptyList(),
            currentPlayer = PlayerColor.WHITE,
            canUndo = false,
            canRedo = false,
        )

    fun setRole(role: ClientRole) {
        this.role = role
    }

    fun setClient(client: Client) {
        this.client = client
    }

    fun onGameStateReceived(payload: GameStatePayload): GuiGameState {
        val newKeys = payload.pieces.map { "${it.row}-${it.col}-${it.color}-${it.pieceId}" }.toSet()
        posToId.keys.retainAll(newKeys)

        val pieces =
            payload.pieces.map { p ->
                val key = "${p.row}-${p.col}-${p.color}-${p.pieceId}"
                val id = posToId.getOrPut(key) { "p${idCounter++}" }
                ChessPiece(
                    id = id,
                    color = if (p.color == "WHITE") PlayerColor.WHITE else PlayerColor.BLACK,
                    position = GuiPosition(p.row + 1, p.col + 1),
                    pieceId = p.pieceId,
                )
            }

        val current = if (payload.currentPlayer == "WHITE") PlayerColor.WHITE else PlayerColor.BLACK

        currentGuiState =
            when (payload.status) {
                GameStatus.WHITE_WINS.name -> GuiGameState.GameOver(PlayerColor.WHITE)
                GameStatus.BLACK_WINS.name -> GuiGameState.GameOver(PlayerColor.BLACK)
                GameStatus.DRAW.name -> GuiGameState.GameOver(PlayerColor.WHITE)
                else -> GuiGameState.Playing(
                    boardSize = BoardSize(payload.boardSize, payload.boardSize),
                    pieces = pieces,
                    currentPlayer = current,
                    canUndo = false,
                    canRedo = false,
                )
            }
        return currentGuiState
    }

    fun process(event: GameInputEvent): GuiGameState =
        when (event) {
            is GameInputEvent.Init -> currentGuiState
            is GameInputEvent.Undo -> currentGuiState
            is GameInputEvent.Redo -> currentGuiState
            is GameInputEvent.MoveEvent -> handleMove(event.move)
        }

    private fun handleMove(guiMove: GuiMove): GuiGameState {
        val state = currentGuiState
        if (role != ClientRole.BLACK || state !is GuiGameState.Playing || state.currentPlayer != PlayerColor.BLACK) {
            return currentGuiState
        }
        client?.send(
            Message(
                "move",
                MovePayload(
                    guiMove.from.row - 1,
                    guiMove.from.column - 1,
                    guiMove.to.row - 1,
                    guiMove.to.column - 1,
                ),
            ),
        )
        return currentGuiState
    }
}
