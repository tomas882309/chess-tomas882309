package edu.austral.dissis.chess.exam

import edu.austral.dissis.chess.factory.ChessGameFactory
import edu.austral.dissis.chess.factory.ChessPieceFactory
import edu.austral.dissis.chess.model.CastlingRights
import edu.austral.dissis.chess.model.ChessMove
import edu.austral.dissis.chess.model.pieces.Bishop
import edu.austral.dissis.chess.model.pieces.King
import edu.austral.dissis.chess.model.pieces.Knight
import edu.austral.dissis.chess.model.pieces.Pawn
import edu.austral.dissis.chess.model.pieces.Queen
import edu.austral.dissis.chess.model.pieces.Rook
import edu.austral.dissis.chess.test.TestBoard
import edu.austral.dissis.chess.test.TestPiece
import edu.austral.dissis.chess.test.TestPieceSymbols
import edu.austral.dissis.chess.test.TestPosition
import edu.austral.dissis.chess.test.TestSize
import edu.austral.dissis.chess.test.game.BlackCheckMate
import edu.austral.dissis.chess.test.game.TestGameRunner
import edu.austral.dissis.chess.test.game.TestMoveDraw
import edu.austral.dissis.chess.test.game.TestMoveFailure
import edu.austral.dissis.chess.test.game.TestMoveResult
import edu.austral.dissis.chess.test.game.TestMoveSuccess
import edu.austral.dissis.chess.test.game.WhiteCheckMate
import edu.austral.dissis.common.game.Game
import edu.austral.dissis.common.model.Board
import edu.austral.dissis.common.model.Color
import edu.austral.dissis.common.model.GameStatus
import edu.austral.dissis.common.model.MoveResult
import edu.austral.dissis.common.model.Piece
import edu.austral.dissis.common.model.PieceKind
import edu.austral.dissis.common.model.Position

class DummyTestGameRunner(
    private val game: Game,
) : TestGameRunner {
    companion object {
        fun create(): DummyTestGameRunner = DummyTestGameRunner(ChessGameFactory.createStandardGame())
    }

    override fun executeMove(
        from: TestPosition,
        to: TestPosition,
    ): TestMoveResult {
        val move = ChessMove.standard(from.toEnginePosition(), to.toEnginePosition())
        return when (val result = game.executeMove(move)) {
            is MoveResult.Failure -> {
                TestMoveFailure(toTestBoard(game.currentState().board()))
            }

            is MoveResult.Success -> {
                when (result.newState().status()) {
                    GameStatus.WHITE_WINS -> WhiteCheckMate(toTestBoard(result.newState().board()))
                    GameStatus.BLACK_WINS -> BlackCheckMate(toTestBoard(result.newState().board()))
                    GameStatus.DRAW -> TestMoveDraw(toTestBoard(result.newState().board()))
                    GameStatus.IN_PROGRESS -> TestMoveSuccess(DummyTestGameRunner(game))
                }
            }
        }
    }

    override fun undo(): TestMoveResult =
        when (game.undo()) {
            is MoveResult.Failure -> TestMoveFailure(toTestBoard(game.currentState().board()))
            is MoveResult.Success -> TestMoveSuccess(DummyTestGameRunner(game))
        }

    override fun redo(): TestMoveResult =
        when (game.redo()) {
            is MoveResult.Failure -> TestMoveFailure(toTestBoard(game.currentState().board()))
            is MoveResult.Success -> TestMoveSuccess(DummyTestGameRunner(game))
        }

    override fun getBoard(): TestBoard = toTestBoard(game.currentState().board())

    override fun withBoard(board: TestBoard): TestGameRunner {
        val engineBoard = board.toEngineBoard()
        val rights = inferCastlingRights(engineBoard)
        return DummyTestGameRunner(ChessGameFactory.createFromBoard(engineBoard, Color.WHITE, rights))
    }

    private fun toTestBoard(board: Board): TestBoard {
        val pieces =
            board.pieces().entries.associate { (pos, piece) ->
                pos.toTestPosition() to piece.toTestPiece()
            }
        return TestBoard(TestSize(board.size(), board.size()), pieces)
    }

    private fun TestBoard.toEngineBoard(): Board {
        val pieces =
            this.pieces.entries.associate { (testPos, testPiece) ->
                testPos.toEnginePosition() to testPiece.toEnginePiece()
            }
        return Board(pieces, size.rows)
    }

    private fun Position.toTestPosition() = TestPosition(row() + 1, col() + 1)

    private fun TestPosition.toEnginePosition() = Position(row - 1, col - 1)

    private fun Piece.toTestPiece() =
        TestPiece(
            kindToSymbol(type()),
            if (color() == Color.WHITE) TestPieceSymbols.WHITE else TestPieceSymbols.BLACK,
        )

    private fun kindToSymbol(kind: PieceKind): Char =
        when (kind) {
            is King -> TestPieceSymbols.KING
            is Queen -> TestPieceSymbols.QUEEN
            is Rook -> TestPieceSymbols.ROOK
            is Bishop -> TestPieceSymbols.BISHOP
            is Knight -> TestPieceSymbols.KNIGHT
            else -> TestPieceSymbols.PAWN
        }

    private fun TestPiece.toEnginePiece(): Piece {
        val color = if (playerColorSymbol == TestPieceSymbols.WHITE) Color.WHITE else Color.BLACK
        return when (pieceTypeSymbol) {
            TestPieceSymbols.KING -> ChessPieceFactory.king(color)
            TestPieceSymbols.QUEEN -> ChessPieceFactory.queen(color)
            TestPieceSymbols.ROOK -> ChessPieceFactory.rook(color)
            TestPieceSymbols.BISHOP -> ChessPieceFactory.bishop(color)
            TestPieceSymbols.KNIGHT -> ChessPieceFactory.knight(color)
            else -> ChessPieceFactory.pawn(color)
        }
    }

    private fun inferCastlingRights(board: Board): CastlingRights {
        val wK = hasKingAt(board, Color.WHITE, 0, 4) && hasRookAt(board, Color.WHITE, 0, 7)
        val wQ = hasKingAt(board, Color.WHITE, 0, 4) && hasRookAt(board, Color.WHITE, 0, 0)
        val bK = hasKingAt(board, Color.BLACK, 7, 4) && hasRookAt(board, Color.BLACK, 7, 7)
        val bQ = hasKingAt(board, Color.BLACK, 7, 4) && hasRookAt(board, Color.BLACK, 7, 0)
        return CastlingRights(wK, wQ, bK, bQ)
    }

    private fun hasKingAt(
        board: Board,
        color: Color,
        row: Int,
        col: Int,
    ) = board
        .pieceAt(Position(row, col))
        .map { it.isColor(color) && it.isType(King.INSTANCE) }
        .orElse(false)

    private fun hasRookAt(
        board: Board,
        color: Color,
        row: Int,
        col: Int,
    ) = board
        .pieceAt(Position(row, col))
        .map { it.isColor(color) && it.isType(Rook.INSTANCE) }
        .orElse(false)
}
