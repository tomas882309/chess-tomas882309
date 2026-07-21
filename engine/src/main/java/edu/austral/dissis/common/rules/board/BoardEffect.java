package edu.austral.dissis.common.rules.board;

import edu.austral.dissis.common.model.Board;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;

public interface BoardEffect {
    boolean appliesTo(Move move, GameState state);
    Board apply(Move move, GameState state, Board board);
}