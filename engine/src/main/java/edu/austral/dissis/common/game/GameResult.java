package edu.austral.dissis.common.game;

public interface GameResult {
    record Moved(Game newGame)      implements GameResult {}
    record Invalid(String reason)   implements GameResult {}
}
