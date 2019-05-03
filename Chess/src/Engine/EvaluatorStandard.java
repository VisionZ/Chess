package Engine;

import static Engine.EvaluationConstants.BISHOP_BONUS;
import static Engine.EvaluationConstants.CASTLE_VALUE;
import static Engine.EvaluationConstants.CHECKED_VALUE;
import static Engine.EvaluationConstants.KING_MOVED_VALUE;
import static Util.ChessConstants.BISHOP_VALUE;
import static Util.ChessConstants.KNIGHT_VALUE;
import static Util.ChessConstants.LEFT_KING_CASTLE_COLUMN;
import static Util.ChessConstants.LENGTH;
import static Util.ChessConstants.PAWN_VALUE;
import static Util.ChessConstants.QUEEN_VALUE;
import static Util.ChessConstants.RIGHT_KING_CASTLE_COLUMN;
import static Util.ChessConstants.ROOK_VALUE;
import java.util.List;

/**
 * Standard evaluator which uses PieceSquareTables.dat 
 * to evaluate positions. This evaluator places a lower 
 * emphasis on positional play, since controlling 
 * a tile is not scored as high here.
 * @author zwill
 */
public final class EvaluatorStandard implements Evaluator {

    public EvaluatorStandard() {

    }

    //apparently, redefining variables instead of always reading them from
    //a seperate class is faster, since you don't have to access another class
    //so redefine static variables in all classes from ChessConstants perse
    //which will allow fast LOCAL read access to same static final variables
    private static final int[] PAWN_POSITION_WHITE = Engine.Tables.PAWN_POSITION_WHITE;
    private static final int[] KNIGHT_POSITION_WHITE = Engine.Tables.KNIGHT_POSITION_WHITE;
    private static final int[] BISHOP_POSITION_WHITE = Engine.Tables.BISHOP_POSITION_WHITE;
    private static final int[] ROOK_POSITION_WHITE = Engine.Tables.ROOK_POSITION_WHITE;
    private static final int[] QUEEN_POSITION_WHITE = Engine.Tables.QUEEN_POSITION_WHITE;
    
    private static final int[] PAWN_POSITION_BLACK = Engine.Tables.PAWN_POSITION_BLACK;
    private static final int[] KNIGHT_POSITION_BLACK = Engine.Tables.KNIGHT_POSITION_BLACK;
    private static final int[] BISHOP_POSITION_BLACK = Engine.Tables.BISHOP_POSITION_BLACK;
    private static final int[] ROOK_POSITION_BLACK = Engine.Tables.ROOK_POSITION_BLACK;
    private static final int[] QUEEN_POSITION_BLACK = Engine.Tables.QUEEN_POSITION_BLACK;
    
    @Override
    public final int evaluateInWhitePerspective(final Board board) {
        AI.DIALOG.increasePositionsScanned();
        return getWhiteScore(board.grid, board.whites) - getBlackScore(board.grid, board.blacks);
    }

    @Override
    public final int evaluateInBlackPerspective(final Board board) {
        AI.DIALOG.increasePositionsScanned();
        return getBlackScore(board.grid, board.blacks) - getWhiteScore(board.grid, board.whites);
    }

    private static int getBlackScore(Grid grid, List<Piece> blacks) {
        final int numberOfBlackPieces = blacks.size();

        int blackScore;
        int blackQueens = 0;
        int blackRooks = 0;
        int blackBishops = 0;
        int blackKnights = 0;
        int blackPawns = 0;

        {
            Piece blackKing = blacks.get(0);
            blackScore = blackKing.getNumberOfAttackTiles(grid);
            int moveCount = blackKing.getMoveCount();
            if (grid.getTile(blackKing.getRow() * LENGTH + blackKing.getColumn()).protectedByWhite()) {
                blackScore -= CHECKED_VALUE;
                if (moveCount > 1) {
                    blackScore -= KING_MOVED_VALUE;
                }
            }
            else if (moveCount > 1) {
                blackScore -= KING_MOVED_VALUE;
            }
            else if (moveCount == 1) {
                switch (blackKing.getColumn()) {
                    case LEFT_KING_CASTLE_COLUMN: {
                        blackScore += CASTLE_VALUE;
                        break;
                    }
                    case RIGHT_KING_CASTLE_COLUMN: {
                        blackScore += CASTLE_VALUE;
                    }
                }
            }
        }

        int index = 1;

        while (index < numberOfBlackPieces) {
            Piece piece = blacks.get(index);
            if (piece.isQueen()) {
                blackScore += piece.getNumberOfProtectedTiles(grid);
                blackScore += piece.getNumberOfAttackTiles(grid);
                blackScore += QUEEN_POSITION_BLACK[piece.getRow() * LENGTH + piece.getColumn()];
                ++blackQueens;
                ++index;
            }
            else {
                break;
            }
        }

        while (index < numberOfBlackPieces) {
            Piece piece = blacks.get(index);
            if (piece.isRook()) {
                blackScore += piece.getNumberOfProtectedTiles(grid);
                blackScore += piece.getNumberOfAttackTiles(grid);
                blackScore += ROOK_POSITION_BLACK[piece.getRow() * LENGTH + piece.getColumn()];
                ++blackRooks;
                ++index;
            }
            else {
                break;
            }
        }

        while (index < numberOfBlackPieces) {
            Piece piece = blacks.get(index);
            if (piece.isBishop()) {
                blackScore += piece.getNumberOfProtectedTiles(grid);
                blackScore += piece.getNumberOfAttackTiles(grid);
                blackScore += BISHOP_POSITION_BLACK[piece.getRow() * LENGTH + piece.getColumn()];
                ++blackBishops;
                ++index;
            }
            else {
                break;
            }
        }

        while (index < numberOfBlackPieces) {
            Piece piece = blacks.get(index);
            if (piece.isKnight()) {
                blackScore += piece.getNumberOfProtectedTiles(grid);
                blackScore += piece.getNumberOfAttackTiles(grid);
                blackScore += KNIGHT_POSITION_BLACK[piece.getRow() * LENGTH + piece.getColumn()];
                ++blackKnights;
                ++index;
            }
            else {
                break;
            }
        }

        while (index < numberOfBlackPieces) {
            Piece piece = blacks.get(index);
            if (piece.isPawn()) {
                blackScore += piece.getNumberOfProtectedTiles(grid);
                blackScore += piece.getNumberOfAttackTiles(grid);
                blackScore += PAWN_POSITION_BLACK[piece.getRow() * LENGTH + piece.getColumn()];
                ++blackPawns;
                ++index;
            }
            else {
                break;
            }
        }

        return blackBishops >= 2
                ? blackScore + (blackQueens * QUEEN_VALUE) + (blackRooks * ROOK_VALUE) + (blackBishops * BISHOP_VALUE) + (blackKnights * KNIGHT_VALUE) + (blackPawns * PAWN_VALUE) + BISHOP_BONUS
                : blackScore + (blackQueens * QUEEN_VALUE) + (blackRooks * ROOK_VALUE) + (blackBishops * BISHOP_VALUE) + (blackKnights * KNIGHT_VALUE) + (blackPawns * PAWN_VALUE);
    }

    private static int getWhiteScore(Grid grid, List<Piece> whites) {
        final int numberOfWhitePieces = whites.size();

        int whiteScore;
        int whiteQueens = 0;
        int whiteRooks = 0;
        int whiteBishops = 0;
        int whiteKnights = 0;
        int whitePawns = 0;

        {
            Piece whiteKing = whites.get(0);
            whiteScore = whiteKing.getNumberOfAttackTiles(grid);
            int moveCount = whiteKing.getMoveCount();
            if (grid.getTile(whiteKing.getRow() * LENGTH + whiteKing.getColumn()).protectedByBlack()) {
                whiteScore -= CHECKED_VALUE;
                if (moveCount > 1) {
                    whiteScore -= KING_MOVED_VALUE;
                }
            }
            else if (moveCount > 1) {
                whiteScore -= KING_MOVED_VALUE;
            }
            else if (moveCount == 1) {
                switch (whiteKing.getColumn()) {
                    case LEFT_KING_CASTLE_COLUMN: {
                        whiteScore += CASTLE_VALUE;
                        break;
                    }
                    case RIGHT_KING_CASTLE_COLUMN: {
                        whiteScore += CASTLE_VALUE;
                    }
                }
            }
        }

        int index = 1;

        while (index < numberOfWhitePieces) {
            Piece piece = whites.get(index);
            if (piece.isQueen()) {
                whiteScore += piece.getNumberOfProtectedTiles(grid);
                whiteScore += piece.getNumberOfAttackTiles(grid);
                whiteScore += QUEEN_POSITION_WHITE[piece.getRow() * LENGTH + piece.getColumn()];
                ++whiteQueens;
                ++index;
            }
            else {
                break;
            }
        }

        while (index < numberOfWhitePieces) {
            Piece piece = whites.get(index);
            if (piece.isRook()) {
                whiteScore += piece.getNumberOfProtectedTiles(grid);
                whiteScore += piece.getNumberOfAttackTiles(grid);
                whiteScore += ROOK_POSITION_WHITE[piece.getRow() * LENGTH + piece.getColumn()];
                ++whiteRooks;
                ++index;
            }
            else {
                break;
            }
        }

        while (index < numberOfWhitePieces) {
            Piece piece = whites.get(index);
            if (piece.isBishop()) {
                whiteScore += piece.getNumberOfProtectedTiles(grid);
                whiteScore += piece.getNumberOfAttackTiles(grid);
                whiteScore += BISHOP_POSITION_WHITE[piece.getRow() * LENGTH + piece.getColumn()];
                ++whiteBishops;
                ++index;
            }
            else {
                break;
            }
        }

        while (index < numberOfWhitePieces) {
            Piece piece = whites.get(index);
            if (piece.isKnight()) {
                whiteScore += piece.getNumberOfProtectedTiles(grid);
                whiteScore += piece.getNumberOfAttackTiles(grid);
                whiteScore += KNIGHT_POSITION_WHITE[piece.getRow() * LENGTH + piece.getColumn()];
                ++whiteKnights;
                ++index;
            }
            else {
                break;
            }
        }

        while (index < numberOfWhitePieces) {
            Piece piece = whites.get(index);
            if (piece.isPawn()) {
                whiteScore += piece.getNumberOfProtectedTiles(grid);
                whiteScore += piece.getNumberOfAttackTiles(grid);
                whiteScore += PAWN_POSITION_WHITE[piece.getRow() * LENGTH + piece.getColumn()];
                ++whitePawns;
                ++index;
            }
            else {
                break;
            }
        }
        
        return whiteBishops >= 2 
                ? whiteScore + (whiteQueens * QUEEN_VALUE) + (whiteRooks * ROOK_VALUE) + (whiteBishops * BISHOP_VALUE) + (whiteKnights * KNIGHT_VALUE) + (whitePawns * PAWN_VALUE) + BISHOP_BONUS
                : whiteScore + (whiteQueens * QUEEN_VALUE) + (whiteRooks * ROOK_VALUE) + (whiteBishops * BISHOP_VALUE) + (whiteKnights * KNIGHT_VALUE) + (whitePawns * PAWN_VALUE);
    }
}