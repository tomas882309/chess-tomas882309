package edu.austral.dissis.chess.ui

import edu.austral.dissis.checkers.factory.CheckersGameFactory
import edu.austral.dissis.checkers.model.CheckersPieceType
import edu.austral.dissis.chess.factory.ChessGameFactory
import edu.austral.dissis.chess.gui.BoardSize
import edu.austral.dissis.chess.gui.CachedImageResolver
import edu.austral.dissis.chess.gui.ChessPiece
import edu.austral.dissis.chess.gui.DefaultImageResolver
import edu.austral.dissis.chess.gui.GameInputEvent
import edu.austral.dissis.chess.gui.GameViewFactory
import edu.austral.dissis.chess.gui.PlayerColor
import edu.austral.dissis.chess.model.ChessMove
import edu.austral.dissis.common.game.Game
import edu.austral.dissis.common.model.Color
import edu.austral.dissis.common.model.GameStatus
import edu.austral.dissis.common.model.MoveResult
import javafx.application.Application
import javafx.application.Application.launch
import javafx.scene.Scene
import javafx.stage.Stage
import edu.austral.dissis.chess.gui.GameState as GuiGameState
import edu.austral.dissis.chess.gui.Move as GuiMove
import edu.austral.dissis.chess.gui.Position as GuiPosition
import edu.austral.dissis.chess.model.PieceType as ChessPieceType
import edu.austral.dissis.common.model.Position as EnginePosition

fun main() {
    launch(ChessGameApplication::class.java)
}

class ChessGameApplication : Application() {
    private val imageResolver = CachedImageResolver(DefaultImageResolver())

    companion object {
        const val GAME_TITLE = "Chess"
    }

    override fun start(primaryStage: Stage) {
        primaryStage.title = GAME_TITLE
        val view = GameViewFactory.create(imageResolver)
        val engine = ChessGuiEngine(CheckersGameFactory.createStandardGame())
        view.inputEvents().subscribe { event -> view.applyState(engine.process(event)) }
        primaryStage.scene = Scene(view.pane())
        primaryStage.show()
    }
}

class ChessGuiEngine(
    private val game: Game,
) {
    private var idCounter = 0
    private var posToStableId: MutableMap<String, String> =
        game
            .currentState()
            .board()
            .pieces()
            .keys
            .associate { pos -> posKey(pos) to "p${idCounter++}" }
            .toMutableMap()

    fun process(event: GameInputEvent): GuiGameState =
        when (event) {
            is GameInputEvent.Init -> {
                buildPlayingState()
            }

            is GameInputEvent.Undo -> {
                game.undo()
                rebuildIds()
                buildPlayingState()
            }

            is GameInputEvent.Redo -> {
                game.redo()
                rebuildIds()
                buildPlayingState()
            }

            is GameInputEvent.MoveEvent -> {
                handleMove(event.move)
            }
        }

    private fun handleMove(guiMove: GuiMove): GuiGameState {
        val engineMove = ChessMove.standard(guiMove.from.toEnginePosition(), guiMove.to.toEnginePosition())
        val prevState = game.currentState()
        return when (val result = game.executeMove(engineMove)) {
            is MoveResult.Failure -> {
                GuiGameState.InvalidMove(result.reason())
            }

            is MoveResult.Success -> {
                updateIds(
                    guiMove,
                    prevState.board().pieces().keys,
                    result
                        .newState()
                        .board()
                        .pieces()
                        .keys,
                )
                when (result.newState().status()) {
                    GameStatus.WHITE_WINS -> GuiGameState.GameOver(PlayerColor.WHITE)
                    GameStatus.BLACK_WINS -> GuiGameState.GameOver(PlayerColor.BLACK)
                    GameStatus.DRAW -> GuiGameState.GameOver(PlayerColor.WHITE)
                    GameStatus.IN_PROGRESS -> buildPlayingState()
                }
            }
        }
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
        posToStableId.remove(toKey)
        posToStableId[toKey] = movingId
        val prevKeyStrings = prevKeys.map { posKey(it) }.toSet()
        val newKeyStrings = newKeys.map { posKey(it) }.toSet()
        prevKeyStrings.subtract(newKeyStrings).forEach { posToStableId.remove(it) }
        newKeyStrings.subtract(prevKeyStrings).forEach { key ->
            if (key !in posToStableId) posToStableId[key] = "p${idCounter++}"
        }
    }

    private fun rebuildIds() {
        val currentKeys =
            game
                .currentState()
                .board()
                .pieces()
                .keys
                .map { posKey(it) }
                .toSet()
        posToStableId.keys.retainAll(currentKeys)
        currentKeys.forEach { key -> if (key !in posToStableId) posToStableId[key] = "p${idCounter++}" }
    }

    private fun buildPlayingState(): GuiGameState.Playing {
        val state = game.currentState()
        val pieces =
            state.board().pieces().map { (pos, piece) ->
                val key = posKey(pos)
                ChessPiece(
                    id = posToStableId[key] ?: "p${idCounter++}",
                    color = if (piece.color() == Color.WHITE) PlayerColor.WHITE else PlayerColor.BLACK,
                    position = GuiPosition(pos.row() + 1, pos.col() + 1),
                    pieceId = piece.type().toPieceId(),
                )
            }
        val current = if (state.currentPlayer() == Color.WHITE) PlayerColor.WHITE else PlayerColor.BLACK
        return GuiGameState.Playing(
            boardSize = BoardSize(state.board().size(), state.board().size()),
            pieces = pieces,
            currentPlayer = current,
            canUndo = game.canUndo(),
            canRedo = game.canRedo(),
        )
    }

    private fun posKey(pos: EnginePosition) = "${pos.row()}-${pos.col()}"

    private fun engineKey(
        row: Int,
        col: Int,
    ) = "$row-$col"

    private fun GuiPosition.toEnginePosition() = EnginePosition(row - 1, column - 1)

    private fun Any.toPieceId(): String =
        when (this) {
            is ChessPieceType -> {
                when (this) {
                    ChessPieceType.KING -> "king"
                    ChessPieceType.QUEEN -> "queen"
                    ChessPieceType.ROOK -> "rook"
                    ChessPieceType.BISHOP -> "bishop"
                    ChessPieceType.KNIGHT -> "knight"
                    ChessPieceType.PAWN -> "pawn"
                }
            }

            is CheckersPieceType -> {
                when (this) {
                    CheckersPieceType.MAN -> "man"
                    CheckersPieceType.KING -> "king"
                }
            }

            else -> {
                "pawn"
            }
        }
}
