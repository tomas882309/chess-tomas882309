package edu.austral.dissis.chess.ui

import edu.austral.dissis.chess.gui.*
import edu.austral.dissis.common.game.Game
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.stage.Stage

class GameApplication : Application() {
    companion object {
        lateinit var gameFactory: () -> Game
        var title: String = ""
    }

    private val imageResolver = CachedImageResolver(DefaultImageResolver())

    override fun start(stage: Stage) {
        stage.title = title
        val view = GameViewFactory.create(imageResolver)
        GameAdapter(gameFactory())
            .process(view.inputEvents())
            .subscribe { Platform.runLater { view.applyState(it) } }
        stage.scene = Scene(view.pane())
        stage.show()
    }
}
