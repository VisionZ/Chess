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

public final class EvaluatorExplicit implements Evaluator {
    
    public EvaluatorExplicit() {

    }

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
        return evaluateInWhitePerspective(new ExplicitBoard(board));
    }

    @Override
    public final int evaluateInBlackPerspective(final Board board) {
        return evaluateInBlackPerspective(new ExplicitBoard(board));
    }

    public final int evaluateInWhitePerspective(ExplicitBoard board) {
        AI.DIALOG.increasePositionsScanned();   
        return getWhiteScore(board) - getBlackScore(board);
    }

    public final int evaluateInBlackPerspective(ExplicitBoard board) {
        AI.DIALOG.increasePositionsScanned();
        return getBlackScore(board) - getWhiteScore(board);
    }
    
    private static int getWhiteScore(ExplicitBoard board) {
        final King whiteKing = board.whiteKing;

        final int whiteQueens = board.whiteQueens.size();
        final int whiteRooks = board.whiteRooks.size();
        final int whiteBishops = board.whiteBishops.size();
        final int whiteKnights = board.whiteKnights.size();
        final int whitePawns = board.whitePawns.size();

        int whiteScore = (whiteQueens * QUEEN_VALUE) + (whiteRooks * ROOK_VALUE) + (whiteBishops * BISHOP_VALUE) + (whiteKnights * KNIGHT_VALUE) + (whitePawns * PAWN_VALUE);
        
        {
            whiteScore += (whiteKing.getNumberOfAttackTiles(board.grid));
            int moveCount = whiteKing.getMoveCount();
            if (board.grid.getTile(whiteKing.getRow() * LENGTH + whiteKing.getColumn()).protectedByBlack()) {
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

        for (int index = 0; index < whiteQueens; ++index) {
            Queen piece = board.whiteQueens.get(index);
            whiteScore += piece.getNumberOfProtectedTiles(board.grid);
            whiteScore += (piece.getNumberOfAttackTiles(board.grid));
            whiteScore += QUEEN_POSITION_WHITE[(piece.getRow() * LENGTH) + piece.getColumn()];
        }

        for (int index = 0; index < whiteRooks; ++index) {
            Rook piece = board.whiteRooks.get(index);
            whiteScore += piece.getNumberOfProtectedTiles(board.grid);
            whiteScore += (piece.getNumberOfAttackTiles(board.grid));
            whiteScore += ROOK_POSITION_WHITE[(piece.getRow() * LENGTH) + piece.getColumn()];
        }

        for (int index = 0; index < whiteBishops; ++index) {
            Bishop piece = board.whiteBishops.get(index);
            whiteScore += piece.getNumberOfProtectedTiles(board.grid);
            whiteScore += (piece.getNumberOfAttackTiles(board.grid));
            whiteScore += BISHOP_POSITION_WHITE[(piece.getRow() * LENGTH) + piece.getColumn()];
        }

        for (int index = 0; index < whiteKnights; ++index) {
            Knight piece = board.whiteKnights.get(index);
            whiteScore += piece.getNumberOfProtectedTiles(board.grid);
            whiteScore += (piece.getNumberOfAttackTiles(board.grid));
            whiteScore += KNIGHT_POSITION_WHITE[(piece.getRow() * LENGTH) + piece.getColumn()];
        }

        for (int index = 0; index < whitePawns; ++index) {
            Pawn piece = board.whitePawns.get(index);
            whiteScore += piece.getNumberOfProtectedTiles(board.grid);
            whiteScore += (piece.getNumberOfAttackTiles(board.grid));
            whiteScore += PAWN_POSITION_WHITE[(piece.getRow() * LENGTH) + piece.getColumn()];
        }

        return whiteBishops >= 2 ? whiteScore + BISHOP_BONUS : whiteScore;
    }

    private static int getBlackScore(ExplicitBoard board) {
        final King blackKing = board.blackKing;

        final int blackQueens = board.blackQueens.size();
        final int blackRooks = board.blackRooks.size();
        final int blackBishops = board.blackBishops.size();
        final int blackKnights = board.blackKnights.size();
        final int blackPawns = board.blackPawns.size();

        int blackScore = (blackQueens * QUEEN_VALUE) + (blackRooks * ROOK_VALUE) + (blackBishops * BISHOP_VALUE) + (blackKnights * KNIGHT_VALUE) + (blackPawns * PAWN_VALUE);

        {
            blackScore += blackKing.getNumberOfAttackTiles(board.grid);
            int moveCount = blackKing.getMoveCount();
            if (board.grid.getTile(blackKing.getRow() * LENGTH + blackKing.getColumn()).protectedByWhite()) {
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

        for (int index = 0; index < blackQueens; ++index) {
            Queen piece = board.blackQueens.get(index);
            blackScore += piece.getNumberOfProtectedTiles(board.grid);
            blackScore += piece.getNumberOfAttackTiles(board.grid);
            blackScore += QUEEN_POSITION_BLACK[(piece.getRow() * LENGTH) + piece.getColumn()];
        }

        for (int index = 0; index < blackRooks; ++index) {
            Rook piece = board.blackRooks.get(index);
            blackScore += piece.getNumberOfProtectedTiles(board.grid);
            blackScore += piece.getNumberOfAttackTiles(board.grid);
            blackScore += ROOK_POSITION_BLACK[(piece.getRow() * LENGTH) + piece.getColumn()];
        }

        for (int index = 0; index < blackBishops; ++index) {
            Bishop piece = board.blackBishops.get(index);
            blackScore += piece.getNumberOfProtectedTiles(board.grid);
            blackScore += piece.getNumberOfAttackTiles(board.grid);
            blackScore += BISHOP_POSITION_BLACK[(piece.getRow() * LENGTH) + piece.getColumn()];
        }

        for (int index = 0; index < blackKnights; ++index) {
            Knight piece = board.blackKnights.get(index);
            blackScore += piece.getNumberOfProtectedTiles(board.grid);
            blackScore += piece.getNumberOfAttackTiles(board.grid);
            blackScore += KNIGHT_POSITION_BLACK[(piece.getRow() * LENGTH) + piece.getColumn()];
        }

        for (int index = 0; index < blackPawns; ++index) {
            Pawn piece = board.blackPawns.get(index);
            blackScore += piece.getNumberOfProtectedTiles(board.grid);
            blackScore += piece.getNumberOfAttackTiles(board.grid);
            blackScore += PAWN_POSITION_BLACK[(piece.getRow() * LENGTH) + piece.getColumn()];
        }

        return blackBishops >= 2 ? blackScore + BISHOP_BONUS : blackScore;
    }
}