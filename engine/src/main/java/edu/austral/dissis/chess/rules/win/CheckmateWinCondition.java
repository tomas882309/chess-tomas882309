package edu.austral.dissis.chess.rules.win;

import edu.austral.dissis.chess.rules.move.KingInCheckDetector;
import edu.austral.dissis.chess.rules.move.KingInCheckValidator;
import edu.austral.dissis.common.model.*;
import edu.austral.dissis.common.rules.move.MoveValidator;
import edu.austral.dissis.common.rules.win.WinCondition;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CheckmateWinCondition implements WinCondition {

    private final KingInCheckDetector detector = new KingInCheckDetector();
    private final MoveValidator moveValidator;

    public CheckmateWinCondition(MoveValidator moveValidator){
        this.moveValidator = moveValidator;
    }

    @Override
    public GameStatus evaluate(GameState state) {
        Color player = state.currentPlayer();
        if(!hasAnyLegalMove(player, state) && detector.isKingInCheck(player, state)){
            return player == Color.WHITE ? GameStatus.BLACK_WINS : GameStatus.WHITE_WINS;
        }
        if (!hasAnyLegalMove(player, state)){
            return GameStatus.DRAW;
        }
        return GameStatus.IN_PROGRESS;
    }

    private boolean hasAnyLegalMove(Color color, GameState state){
        return state.board().pieces().entrySet().stream()
                .filter(e -> e.getValue().isColor(color))
                .anyMatch(e -> pieceHasLegalMove(e.getKey(), state));

    }

    private boolean pieceHasLegalMove(Position from, GameState state){
        return allPositions(state)
                .anyMatch(to -> moveValidator
                        .findViolation(new Move(from, to), state).isEmpty());

    }

    private Stream<Position> allPositions(GameState state){
        return IntStream.range(0, state.board().rows())
                .boxed()
                .flatMap(r -> IntStream.range(0, state.board().cols())
                        .mapToObj(c -> new Position(r, c)));
    }
}
