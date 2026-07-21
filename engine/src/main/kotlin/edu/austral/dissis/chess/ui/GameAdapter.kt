package edu.austral.dissis.chess.ui

import edu.austral.dissis.chess.gui.GameInputEvent
import edu.austral.dissis.chess.gui.Move as UiMove
import edu.austral.dissis.chess.gui.GameState as GuiGameState
import edu.austral.dissis.common.model.Move as EngineMove
import edu.austral.dissis.common.game.Game
import edu.austral.dissis.common.game.GameResult
import io.reactivex.rxjava3.core.Observable


class GameAdapter(private var game: Game) {

    fun process(inputs: Observable<GameInputEvent>): Observable<GuiGameState> =
        inputs.map<GuiGameState> { event ->
            when (event) {
                is GameInputEvent.Init -> UIMapper.toPlayingState(game)
                is GameInputEvent.MoveEvent -> handleMove(event.move)
                is GameInputEvent.Undo -> { game = game.undo(); UIMapper.toPlayingState(game) }
                is GameInputEvent.Redo -> { game = game.redo(); UIMapper.toPlayingState(game) }
            }
        }

    private fun handleMove(uiMove: UiMove): GuiGameState {
        val move = UIMapper.toEngineMove(uiMove)
        return when (val result = game.executeMove(move)) {
            is GameResult.Moved -> {
                game = result.newGame()
                UIMapper.toPlayingOrGameOver(game)
            }
            is GameResult.Invalid -> GuiGameState.InvalidMove(result.reason())
            else -> GuiGameState.InvalidMove("Error inesperado")
        }
    }

    fun currentGame(): Game = game

    @Synchronized
    fun applyNetworkMove(move: EngineMove): GuiGameState? {
        val result = game.executeMove(move)
        return if (result is GameResult.Moved) {
            game = result.newGame()
            UIMapper.toPlayingOrGameOver(game)
        } else null
    }
}
