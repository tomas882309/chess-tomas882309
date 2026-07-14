package edu.austral.dissis.checkers.factory;

import edu.austral.dissis.checkers.game.CheckersGameEngine;
import edu.austral.dissis.checkers.model.CheckersExtra;
import edu.austral.dissis.checkers.rules.CheckersBoardUpdater;
import edu.austral.dissis.checkers.rules.CheckersMoveValidator;
import edu.austral.dissis.checkers.rules.CheckersWinCondition;
import edu.austral.dissis.common.game.Game;
import edu.austral.dissis.common.model.Board;
import edu.austral.dissis.common.model.Color;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.GameStatus;
import edu.austral.dissis.common.model.Piece;
import edu.austral.dissis.common.model.Position;
import edu.austral.dissis.common.rules.StandardTurnManager;
import edu.austral.dissis.common.rules.TurnManager;
import edu.austral.dissis.common.rules.WinCondition;

import java.util.HashMap;
import java.util.Map;

public class CheckersGameFactory {

  public static Game createStandardGame() {
    return buildGame(buildInitialBoard(), new StandardTurnManager(), new CheckersWinCondition());
  }

  private static Game buildGame(Board board, TurnManager turnManager, WinCondition winCondition) {
    GameState state = new GameState(board, Color.WHITE, GameStatus.IN_PROGRESS, CheckersExtra.none());
    CheckersGameEngine engine = new CheckersGameEngine(
            new CheckersMoveValidator(),
            winCondition,
            turnManager,
            new CheckersBoardUpdater());
    return new Game(engine, state);
  }

  private static Board buildInitialBoard() {
    Map<Position, Piece> pieces = new HashMap<>();
    for (int row = 0; row < 3; row++) {
      placePiecesOnDarkSquares(pieces, row, Color.WHITE);
    }
    for (int row = 5; row < 8; row++) {
      placePiecesOnDarkSquares(pieces, row, Color.BLACK);
    }
    return new Board(pieces, 8);
  }

  private static void placePiecesOnDarkSquares(Map<Position, Piece> pieces, int row, Color color) {
    for (int col = 0; col < 8; col++) {
      if ((row + col) % 2 == 1) {
        pieces.put(new Position(row, col), CheckersPieceFactory.man(color));
      }
    }
  }
}
