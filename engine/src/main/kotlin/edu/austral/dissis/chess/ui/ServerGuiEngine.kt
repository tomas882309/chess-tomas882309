package edu.austral.dissis.chess.ui

import edu.austral.dissis.chess.gui.BoardSize
import edu.austral.dissis.chess.gui.ChessPiece
import edu.austral.dissis.chess.gui.GameInputEvent
import edu.austral.dissis.chess.gui.PlayerColor
import edu.austral.dissis.common.model.Color
import edu.austral.dissis.common.model.GameStatus
import edu.austral.dissis.common.model.MoveResult
import edu.austral.dissis.common.model.Piece
import edu.austral.dissis.server.GameServerState
import edu.austral.dissis.chess.gui.GameState as GuiGameState
import edu.austral.dissis.chess.gui.Move as GuiMove
import edu.austral.dissis.chess.gui.Position as GuiPosition
import edu.austral.dissis.common.model.Position as EnginePosition

class ServerGuiEngine(
    private val serverState: GameServerState,
) {
    private var idCounter = 0
    private var posToStableId: MutableMap<String, String> = mutableMapOf()
    private var posToMeta: MutableMap<String, String> = mutableMapOf()

    init {
        serverState.currentGameState().board().pieces().forEach { (pos, piece) ->
            val key = posKey(pos)
            posToStableId[key] = "p${idCounter++}"
            posToMeta[key] = pieceMeta(piece)
        }
    }

    fun process(event: GameInputEvent): GuiGameState =
        when (event) {
            is GameInputEvent.Init -> buildCurrentGuiState()
            is GameInputEvent.Undo -> {
                serverState.undo()
                rebuildIds()
                serverState.broadcastGameState()
                buildCurrentGuiState()
            }
            is GameInputEvent.Redo -> {
                serverState.redo()
                rebuildIds()
                serverState.broadcastGameState()
                buildCurrentGuiState()
            }
            is GameInputEvent.MoveEvent -> handleLocalMove(event.move)
        }

    private fun handleLocalMove(guiMove: GuiMove): GuiGameState {
        if (serverState.currentGameState().currentPlayer() != Color.WHITE) {
            return buildCurrentGuiState()
        }
        val from = guiMove.from.toEnginePosition()
        val to = guiMove.to.toEnginePosition()
        val prevKeys = serverState.currentGameState().board().pieces().keys
        return when (val result = serverState.applyMove(from, to)) {
            is MoveResult.Failure -> GuiGameState.InvalidMove(result.reason())
            is MoveResult.Success -> {
                updateIds(guiMove, prevKeys, result.newState().board().pieces().keys)
                serverState.broadcastGameState()
                when (result.newState().status()) {
                    GameStatus.WHITE_WINS -> GuiGameState.GameOver(PlayerColor.WHITE)
                    GameStatus.BLACK_WINS -> GuiGameState.GameOver(PlayerColor.BLACK)
                    GameStatus.DRAW -> GuiGameState.GameOver(PlayerColor.WHITE)
                    GameStatus.IN_PROGRESS -> buildCurrentGuiState()
                }
            }
        }
    }

    fun rebuildIds() {
        val currentBoard = serverState.currentGameState().board()
        val currentKeys = currentBoard.pieces().keys.map { posKey(it) }.toSet()
        posToStableId.keys.retainAll(currentKeys)
        posToMeta.keys.retainAll(currentKeys)
        currentBoard.pieces().forEach { (pos, piece) ->
            val key = posKey(pos)
            val meta = pieceMeta(piece)
            if (posToMeta[key] != meta) {
                posToStableId[key] = "p${idCounter++}"
                posToMeta[key] = meta
            } else if (key !in posToStableId) {
                posToStableId[key] = "p${idCounter++}"
                posToMeta[key] = meta
            }
        }
    }

    fun buildCurrentGuiState(): GuiGameState.Playing {
        val state = serverState.currentGameState()
        val pieces = state.board().pieces().map { (pos, piece) ->
            val key = posKey(pos)
            ChessPiece(
                id = posToStableId[key] ?: "p${idCounter++}",
                color = if (piece.color() == Color.WHITE) PlayerColor.WHITE else PlayerColor.BLACK,
                position = GuiPosition(pos.row() + 1, pos.col() + 1),
                pieceId = piece.type().pieceId(),
            )
        }
        val current = if (state.currentPlayer() == Color.WHITE) PlayerColor.WHITE else PlayerColor.BLACK
        return GuiGameState.Playing(
            boardSize = BoardSize(state.board().size(), state.board().size()),
            pieces = pieces,
            currentPlayer = current,
            canUndo = serverState.canUndo(),
            canRedo = serverState.canRedo(),
        )
    }

    private fun updateIds(
        guiMove: GuiMove,
        prevKeys: Set<EnginePosition>,
        newKeys: Set<EnginePosition>,
    ) {
        val fromKey = engineKey(guiMove.from.row - 1, guiMove.from.column - 1)
        val toKey = engineKey(guiMove.to.row - 1, guiMove.to.column - 1)
        val movingId = posToStableId[fromKey] ?: "p${idCounter++}"
        posToStableId.remove(fromKey)
        posToMeta.remove(fromKey)
        posToStableId.remove(toKey)
        posToMeta.remove(toKey)
        posToStableId[toKey] = movingId
        val currentBoard = serverState.currentGameState().board()
        currentBoard.pieceAt(EnginePosition(guiMove.to.row - 1, guiMove.to.column - 1))
            .ifPresent { posToMeta[toKey] = pieceMeta(it) }
        val prevKeyStrings = prevKeys.map { posKey(it) }.toSet()
        val newKeyStrings = newKeys.map { posKey(it) }.toSet()
        prevKeyStrings.subtract(newKeyStrings).forEach {
            posToStableId.remove(it)
            posToMeta.remove(it)
        }
        newKeyStrings.subtract(prevKeyStrings).forEach { key ->
            if (key !in posToStableId) {
                posToStableId[key] = "p${idCounter++}"
            }
        }
    }

    private fun pieceMeta(piece: Piece) = "${piece.color()}-${piece.type().pieceId()}"

    private fun posKey(pos: EnginePosition) = "${pos.row()}-${pos.col()}"

    private fun engineKey(row: Int, col: Int) = "$row-$col"

    private fun GuiPosition.toEnginePosition() = EnginePosition(row - 1, column - 1)
}