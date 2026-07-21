package edu.austral.dissis.chess.ui

import edu.austral.dissis.chess.factory.ChessGameFactory
import javafx.application.Application

fun main() {
    GameApplication.gameFactory = { ChessGameFactory.createStandardGame() }
    GameApplication.title = "Chess"
    Application.launch(GameApplication::class.java)
}
