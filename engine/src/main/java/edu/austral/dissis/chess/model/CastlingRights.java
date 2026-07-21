package edu.austral.dissis.chess.model;

import edu.austral.dissis.common.model.Color;

public record CastlingRights(boolean whiteKingside, boolean whiteQueenside, boolean blackKingside, boolean blackQueenside) {

    public static CastlingRights allEnabled() {
        return new CastlingRights(true, true, true, true);
    }

    public boolean canCastleKingside(Color color) {
        return color == Color.WHITE ? whiteKingside : blackKingside;
    }

    public boolean canCastleQueenside(Color color) {
        return color == Color.WHITE ? whiteQueenside : blackQueenside;
    }

    public CastlingRights revokeAll(Color color) {
        return color == Color.WHITE
                ? new CastlingRights(false, false, blackKingside, blackQueenside)
                : new CastlingRights(whiteKingside, whiteQueenside, false, false);
    }

    public CastlingRights revokeKingside(Color color) {
        return color == Color.WHITE
                ? new CastlingRights(false, whiteQueenside, blackKingside, blackQueenside)
                : new CastlingRights(whiteKingside, whiteQueenside, false, blackQueenside);
    }

    public CastlingRights revokeQueenside(Color color) {
        return color == Color.WHITE
                ? new CastlingRights(whiteKingside, false, blackKingside, blackQueenside)
                : new CastlingRights(whiteKingside, whiteQueenside, blackKingside, false);
    }
}
