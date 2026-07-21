package edu.austral.dissis.chess.ui

import edu.austral.dissis.chess.gui.BoardSize
import edu.austral.dissis.chess.gui.ChessPiece
import edu.austral.dissis.chess.gui.GameState
import edu.austral.dissis.chess.gui.PlayerColor
import edu.austral.dissis.chess.gui.Move as UiMove
import edu.austral.dissis.chess.gui.Position as UiPosition
import edu.austral.dissis.common.game.Game
import edu.austral.dissis.common.model.Board
import edu.austral.dissis.common.model.Color
import edu.austral.dissis.common.model.GameStatus
import edu.austral.dissis.common.model.Move as EngineMove
import edu.austral.dissis.common.model.Position as EnginePosition


object UIMapper {

    fun toPlayingState(game: Game): GameState.Playing {
        val state = game.current()
        return GameState.Playing(
            boardSize = BoardSize(state.board().cols(), state.board().rows()),
            pieces = toPieces(state.board()),
            currentPlayer = toPlayerColor(state.currentPlayer()),
            canUndo = game.canUndo(),
            canRedo = game.canRedo()
        )
    }

    fun toPlayingOrGameOver(game: Game): GameState {
        val state = game.current()
        return when (state.status()) {
            GameStatus.WHITE_WINS -> GameState.GameOver(PlayerColor.WHITE)
            GameStatus.BLACK_WINS -> GameState.GameOver(PlayerColor.BLACK)
            else -> toPlayingState(game)
        }
    }

    fun toEngineMove(uiMove: UiMove): EngineMove =
        EngineMove(toEnginePosition(uiMove.from), toEnginePosition(uiMove.to))


    private fun toPieces(board: Board): List<ChessPiece> =
        board.pieces().entries.map { (pos, piece) ->
            ChessPiece(
                id = piece.id(),
                color = toPlayerColor(piece.color()),
                position = toUiPosition(pos),
                pieceId = piece.type().pieceId()
            )
        }

    private fun toPlayerColor(color: Color): PlayerColor =
        if (color == Color.WHITE) PlayerColor.WHITE else PlayerColor.BLACK

    private fun toEnginePosition(uiPos: UiPosition): EnginePosition =
        EnginePosition(uiPos.row - 1, uiPos.column - 1)

    private fun toUiPosition(pos: EnginePosition): UiPosition =
        UiPosition(pos.row() + 1, pos.col() + 1)
}
