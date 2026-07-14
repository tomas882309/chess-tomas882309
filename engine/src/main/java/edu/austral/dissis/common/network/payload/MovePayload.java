package edu.austral.dissis.common.network.payload;

public record MovePayload(int fromRow, int fromCol, int toRow, int toCol) {}
