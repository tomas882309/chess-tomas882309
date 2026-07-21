package edu.austral.dissis.chess.ui

import edu.austral.dissis.chess.factory.ChessGameFactory
import edu.austral.dissis.chess.factory.ExtinctionGameFactory
import javafx.application.Application

fun main() {
    GameApplication.gameFactory = { ExtinctionGameFactory.createStandardGame() }
    GameApplication.title = "Chess"
    Application.launch(GameApplication::class.java)
}
