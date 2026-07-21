package edu.austral.dissis.checkers.ui

import edu.austral.dissis.checkers.factory.CheckersGameFactory
import edu.austral.dissis.chess.ui.GameApplication
import javafx.application.Application

fun main() {
    GameApplication.gameFactory = { CheckersGameFactory.createStandardGame() }
    GameApplication.title = "Checkers"
    Application.launch(GameApplication::class.java)
}

/* capa blanca (tablero y pieza nueva) y extinction (wincondition)*/