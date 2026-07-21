package edu.austral.dissis.common.rules.board;

import edu.austral.dissis.common.model.Board;
import edu.austral.dissis.common.model.GameState;
import edu.austral.dissis.common.model.Move;

import java.util.List;

public class BoardUpdater {
    private final List<BoardEffect> effects;

    public BoardUpdater(List<BoardEffect> effects) {
        this.effects = List.copyOf(effects);
    }

    public Board apply(Move move, GameState state) {
        Board board = state.board().boardWithMove(move.from(), move.to());
        for (BoardEffect effect : effects){
            if (effect.appliesTo(move, state)) {
                board = effect.apply(move, state, board);
            }
        }
        return board;
    }
}