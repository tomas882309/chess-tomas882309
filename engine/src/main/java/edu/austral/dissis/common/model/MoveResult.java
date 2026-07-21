package edu.austral.dissis.common.model;

public sealed interface MoveResult {
    record Success(GameState newState) implements MoveResult {}
    record Failure(String reason) implements MoveResult {}
}
