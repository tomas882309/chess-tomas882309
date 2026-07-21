package edu.austral.dissis.chess.exam

import edu.austral.dissis.chess.factory.ChessGameFactory
import edu.austral.dissis.chess.factory.ChessPieceFactory
import edu.austral.dissis.chess.model.CastlingRights
import edu.austral.dissis.chess.model.pieces.*
import edu.austral.dissis.chess.test.*
import edu.austral.dissis.chess.test.game.*
import edu.austral.dissis.common.game.Game
import edu.austral.dissis.common.game.GameResult
import edu.austral.dissis.common.model.*
import java.util.Optional

class DummyTestGameRunner(
    private val game: Game,
) : TestGameRunner {

    companion object {
        fun create(): DummyTestGameRunner = DummyTestGameRunner(ChessGameFactory.createStandardGame())
    }

    override fun executeMove(from: TestPosition, to: TestPosition): TestMoveResult {
        val move = Move(from.toEnginePosition(), to.toEnginePosition())
        return when (val result = game.executeMove(move)) {
            is GameResult.Invalid -> TestMoveFailure(toTestBoard(game.current().board()))
            is GameResult.Moved -> when (result.newGame().current().status()) {
                GameStatus.WHITE_WINS -> WhiteCheckMate(toTestBoard(result.newGame().current().board()))
                GameStatus.BLACK_WINS -> BlackCheckMate(toTestBoard(result.newGame().current().board()))
                GameStatus.DRAW -> TestMoveDraw(toTestBoard(result.newGame().current().board()))
                GameStatus.IN_PROGRESS -> TestMoveSuccess(DummyTestGameRunner(result.newGame()))
                else -> TestMoveFailure(toTestBoard(result.newGame().current().board()))
            }
            else -> TestMoveFailure(toTestBoard(game.current().board()))
        }
    }

    override fun undo(): TestMoveResult {
        if (!game.canUndo()) return TestMoveFailure(toTestBoard(game.current().board()))
        return TestMoveSuccess(DummyTestGameRunner(game.undo()))
    }

    override fun redo(): TestMoveResult {
        if (!game.canRedo()) return TestMoveFailure(toTestBoard(game.current().board()))
        return TestMoveSuccess(DummyTestGameRunner(game.redo()))
    }

    override fun getBoard(): TestBoard = toTestBoard(game.current().board())

    override fun withBoard(board: TestBoard): TestGameRunner {
        val engineBoard = board.toEngineBoard()
        val rights = inferCastlingRights(engineBoard)
        return DummyTestGameRunner(ChessGameFactory.createFromBoard(engineBoard, Color.WHITE, rights, Optional.empty()))    }

    private fun toTestBoard(board: Board): TestBoard {
        val pieces = board.pieces().entries.associate { (pos, piece) ->
            pos.toTestPosition() to piece.toTestPiece()
        }
        return TestBoard(TestSize(board.rows(), board.cols()), pieces)
    }

    private fun TestBoard.toEngineBoard(): Board {
        val pieces = this.pieces.entries.associate { (testPos, testPiece) ->
            testPos.toEnginePosition() to testPiece.toEnginePiece()
        }
        return Board(pieces, size.rows, size.cols)
    }

    private fun Position.toTestPosition() = TestPosition(row() + 1, col() + 1)
    private fun TestPosition.toEnginePosition() = Position(row - 1, col - 1)

    private fun Piece.toTestPiece() = TestPiece(
        kindToSymbol(type()),
        if (color() == Color.WHITE) TestPieceSymbols.WHITE else TestPieceSymbols.BLACK,
    )

    private fun kindToSymbol(kind: PieceKind): Char = when (kind) {
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

    private fun hasKingAt(board: Board, color: Color, row: Int, col: Int) =
        board.pieceAt(Position(row, col)).map { it.isColor(color) && it.isType(King.INSTANCE) }.orElse(false)

    private fun hasRookAt(board: Board, color: Color, row: Int, col: Int) =
        board.pieceAt(Position(row, col)).map { it.isColor(color) && it.isType(Rook.INSTANCE) }.orElse(false)
}