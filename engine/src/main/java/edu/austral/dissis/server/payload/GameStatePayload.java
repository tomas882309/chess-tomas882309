package edu.austral.dissis.server.payload;

import java.util.List;

public record GameStatePayload(
        List<PiecePayload> pieces,
        String currentPlayer,
        String status,
        int boardRows,
        int boardCols) {}
