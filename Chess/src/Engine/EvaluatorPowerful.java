package Engine;

import static Engine.EvaluationConstants.BISHOP_BONUS;
import static Engine.EvaluationConstants.BISHOP_MOBILITY;
import static Engine.EvaluationConstants.CASTLE_VALUE;
import static Engine.EvaluationConstants.CHECKED_VALUE;
import static Engine.EvaluationConstants.KING_MOBILITY;
import static Engine.EvaluationConstants.KING_MOVED_VALUE;
import static Engine.EvaluationConstants.KNIGHT_MOBILITY;
import static Engine.EvaluationConstants.PAWN_MOBILITY;
import static Engine.EvaluationConstants.QUEEN_MOBILITY;
import static Engine.EvaluationConstants.ROOK_MOBILITY;
import static Util.ChessConstants.BISHOP_VALUE;
import static Util.ChessConstants.KNIGHT_VALUE;
import static Util.ChessConstants.LEFT_KING_CASTLE_COLUMN;
import static Util.ChessConstants.LENGTH;
import static Util.ChessConstants.LINEAR_LENGTH;
import static Util.ChessConstants.PAWN_VALUE;
import static Util.ChessConstants.QUEEN_VALUE;
import static Util.ChessConstants.RIGHT_KING_CASTLE_COLUMN;
import static Util.ChessConstants.ROOK_VALUE;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Strong evaluator that emphasizes important pawn control, protection of
 * pieces, mobility and positional play. When using this evaluator AlphaBeta
 * search speed is sacrificed for precision.
 *
 * Tables inspired from:
 * https://chessprogramming.wikispaces.com/Simplified+evaluation+function#Piece-Square%20Tables
 * https://github.com/bytefire/Shutranj/blob/master/Shutranj.Engine/Evaluation2.cs
 *
 * With minor modifications
 *
 * @author zwill
 */
public final class EvaluatorPowerful implements Evaluator {

    public EvaluatorPowerful() {

    }

    static final int[] PAWN_POSITION_WHITE = {
        0, 0, 0, 0, 0, 0, 0, 0,
        50, 50, 50, 50, 50, 50, 50, 50,
        10, 10, 20, 30, 30, 20, 10, 10,
        5, 5, 10, 25, 25, 10, 5, 5,
        0, 0, 0, 20, 20, 0, 0, 0,
        5, -5, -10, 0, 0, -10, -5, 5,
        5, 10, 10, -20, -20, 10, 10, 5,
        0, 0, 0, 0, 0, 0, 0, 0
    };

    static final int[] KNIGHT_POSITION_WHITE = {
        -50, -40, -30, -30, -30, -30, -40, -50,
        -40, -20, 0, 0, 0, 0, -20, -40,
        -30, 7, 10, 15, 15, 10, 7, -30,
        -30, 7, 15, 20, 20, 15, 7, -30,
        -30, 7, 15, 20, 20, 15, 7, -30,
        -30, 7, 10, 15, 15, 10, 7, -30,
        -40, -20, 0, 5, 5, 0, -20, -40,
        -50, -40, -30, -30, -30, -30, -40, -50
    };

    static final int[] BISHOP_POSITION_WHITE = {
        -20, -10, -10, -10, -10, -10, -10, -20,
        -10, 0, 0, 0, 0, 0, 0, -10,
        -10, 0, 5, 10, 10, 5, 0, -10,
        -10, 5, 5, 10, 10, 5, 5, -10,
        -10, 0, 10, 10, 10, 10, 0, -10,
        -10, 10, 10, 10, 10, 10, 10, -10,
        -10, 5, 0, 0, 0, 0, 5, -10,
        -20, -10, -40, -10, -10, -40, -10, -20
    };

    static final int[] ROOK_POSITION_WHITE = {
        0, 0, 0, 0, 0, 0, 0, 0,
        5, 10, 10, 10, 10, 10, 10, 5,
        -5, 0, 0, 0, 0, 0, 0, -5,
        -5, 0, 0, 0, 0, 0, 0, -5,
        -5, 0, 0, 0, 0, 0, 0, -5,
        -5, 0, 0, 0, 0, 0, 0, -5,
        -5, 0, 0, 0, 0, 0, 0, -5,
        0, 0, 0, 5, 5, 0, 0, 0
    };

    static final int[] QUEEN_POSITION_WHITE = {
        -20, -10, -10, -5, -5, -10, -10, -20,
        -10, 0, 0, 0, 0, 0, 0, -10,
        -10, 0, 5, 5, 5, 5, 0, -10,
        -5, 0, 5, 5, 5, 5, 0, -5,
        -5, 0, 5, 5, 5, 5, 0, -5,
        -10, 0, 5, 5, 5, 5, 0, -10,
        -10, 0, 0, 0, 0, 0, 0, -10,
        -20, -10, -10, -5, -5, -10, -10, -20
    };

    static final int[] KING_POSITION_WHITE_MID = {
        -30, -40, -40, -50, -50, -40, -40, -30,
        -30, -40, -40, -50, -50, -40, -40, -30,
        -30, -40, -40, -50, -50, -40, -40, -30,
        -30, -40, -40, -50, -50, -40, -40, -30,
        -20, -30, -30, -40, -40, -30, -30, -20,
        -10, -20, -20, -20, -20, -20, -20, -10,
        20, 20, 0, 0, 0, 0, 20, 20,
        20, 30, 10, 0, 0, 10, 30, 20
    };

    static final int[] KING_POSITION_WHITE_END = {
        -50, -40, -30, -20, -20, -30, -40, -50,
        -30, -20, -10, 0, 0, -10, -20, -30,
        -30, -10, 20, 30, 30, 20, -10, -30,
        -30, -10, 30, 40, 40, 30, -10, -30,
        -30, -10, 30, 40, 40, 30, -10, -30,
        -30, -10, 20, 30, 30, 20, -10, -30,
        -30, -30, 0, 0, 0, 0, -30, -30,
        -50, -30, -30, -30, -30, -30, -30, -50
    };

    static final int[] PAWN_POSITION_BLACK = new int[LINEAR_LENGTH];
    static final int[] KNIGHT_POSITION_BLACK = new int[LINEAR_LENGTH];
    static final int[] BISHOP_POSITION_BLACK = new int[LINEAR_LENGTH];
    static final int[] ROOK_POSITION_BLACK = new int[LINEAR_LENGTH];
    static final int[] QUEEN_POSITION_BLACK = new int[LINEAR_LENGTH];
    static final int[] KING_POSITION_BLACK_MID = new int[LINEAR_LENGTH];
    static final int[] KING_POSITION_BLACK_END = new int[LINEAR_LENGTH];

    private static final int[] LITTLE_ENDIAN_RANK_FILE_MAPPING = {
        56, 57, 58, 59, 60, 61, 62, 63,
        48, 49, 50, 51, 52, 53, 54, 55,
        40, 41, 42, 43, 44, 45, 46, 47,
        32, 33, 34, 35, 36, 37, 38, 39,
        24, 25, 26, 27, 28, 29, 30, 31,
        16, 17, 18, 19, 20, 21, 22, 23,
        8, 9, 10, 11, 12, 13, 14, 15,
        0, 1, 2, 3, 4, 5, 6, 7
    };

    static {
        for (int index = 0; index < LINEAR_LENGTH; ++index) {
            final int mirrorIndex = LITTLE_ENDIAN_RANK_FILE_MAPPING[index];
            PAWN_POSITION_BLACK[mirrorIndex] = PAWN_POSITION_WHITE[index];
            KNIGHT_POSITION_BLACK[mirrorIndex] = KNIGHT_POSITION_WHITE[index];
            BISHOP_POSITION_BLACK[mirrorIndex] = BISHOP_POSITION_WHITE[index];
            ROOK_POSITION_BLACK[mirrorIndex] = ROOK_POSITION_WHITE[index];
            QUEEN_POSITION_BLACK[mirrorIndex] = QUEEN_POSITION_WHITE[index];
            KING_POSITION_BLACK_MID[mirrorIndex] = KING_POSITION_WHITE_MID[index];
            KING_POSITION_BLACK_END[mirrorIndex] = KING_POSITION_WHITE_END[index];
        }
        mirroredColumnsEqual(toMatrix(PAWN_POSITION_WHITE), toMatrix(PAWN_POSITION_BLACK));
        mirroredColumnsEqual(toMatrix(BISHOP_POSITION_WHITE), toMatrix(BISHOP_POSITION_BLACK));
        mirroredColumnsEqual(toMatrix(KNIGHT_POSITION_WHITE), toMatrix(KNIGHT_POSITION_BLACK));
        mirroredColumnsEqual(toMatrix(ROOK_POSITION_WHITE), toMatrix(ROOK_POSITION_BLACK));
        mirroredColumnsEqual(toMatrix(QUEEN_POSITION_WHITE), toMatrix(QUEEN_POSITION_BLACK));
        mirroredColumnsEqual(toMatrix(KING_POSITION_WHITE_MID), toMatrix(KING_POSITION_BLACK_MID));
        mirroredColumnsEqual(toMatrix(KING_POSITION_WHITE_END), toMatrix(KING_POSITION_BLACK_END));
        System.out.println("EvaluatorPowerful Table Checking Complete.");
    }

    private static int[][] toMatrix(int[] nums) {
        int[][] matrix = new int[LENGTH][LENGTH];
        int row = 0;
        int column = 0;
        for (int n : nums) {
            if (column == LENGTH) {
                row++;
                column = 0;
            }
            matrix[row][column++] = n;
        }
        return matrix;
    }

    private static void mirroredColumnsEqual(int[][] mat1, int[][] mat2) {
        int column = 0;
        for (int times = 0; times < LENGTH; ++times) {
            List<Integer> vertical = new java.util.ArrayList<>(LENGTH);
            List<Integer> otherVertical = new java.util.ArrayList<>(LENGTH);
            for (int row = 0; row < LENGTH; ++row) {
                vertical.add(mat1[row][column]);
                otherVertical.add(mat2[row][column]);
            }
            java.util.Collections.reverse(otherVertical);
            if (!vertical.equals(otherVertical)) {
                throw new Error();
            }
            column++;
        }
    }

    static final Map<Grid, Integer> DATABASE = new HashMap<>();

    @Override
    public final int evaluateInWhitePerspective(final Board board) {
        AI.DIALOG.increasePositionsScanned();
        return getWhiteScoreUnsorted(board.grid, board.whites) - getBlackScoreUnsorted(board.grid, board.blacks);
    }

    @Override
    public final int evaluateInBlackPerspective(final Board board) {
        AI.DIALOG.increasePositionsScanned();
        return getBlackEvaluation(board.grid, board.whites, board.blacks);
    }

    /**
     * Returns a penalty or zero depending on whether the given piece is exposed
     * or not. Here, "exposed" means that a piece is not defended by a friendly
     * piece and is at the same time, under threat of being captured by an enemy
     * piece.
     *
     * @param grid The chess board.
     * @param piece The given piece.
     * @return A penalty or zero depending on whether the given piece is exposed
     * or not.
     */
    private static int exposed(Grid grid, Piece piece) {
        Tile tile = grid.getTile(piece.getRow(), piece.getColumn());
        return !tile.protectedByAlly(piece) && tile.protectedByEnemy(piece)
                ? -(piece.getValue() / 2) //not a total loss, but still a penalty
                : 0; //no penalty
    }
    
    //forks 
    //pins, attacking an enemy piece that is blocking the vision of the king
    //skewer 
    //
    
    
    private static int getBlackEvaluation(Grid grid, List<Piece> whites, List<Piece> blacks) {
        int blackScore = 0;
        int blackQueens = 0;
        int blackRooks = 0;
        int blackBishops = 0;
        int blackKnights = 0;
        int blackPawns = 0;

        int whiteScore = 0;
        int whiteQueens = 0;
        int whiteRooks = 0;
        int whiteBishops = 0;
        int whiteKnights = 0;
        int whitePawns = 0;

        {
            //king must be first
            final Piece blackKing = blacks.get(0);
            final int blackKingColumn = blackKing.getColumn();
            final int blackKingLocation = blackKing.getRow() * LENGTH + blackKingColumn;
            {
                int moveCount = blackKing.getMoveCount();
                if (moveCount > 1) {
                    blackScore -= KING_MOVED_VALUE;
                }
                if (grid.getTile(blackKingLocation).protectedByWhite()) {
                    blackScore -= CHECKED_VALUE;
                }
                else if (moveCount == 1) {
                    //only award castling bonus if king is not under siege
                    if (blackKingColumn == LEFT_KING_CASTLE_COLUMN || blackKingColumn == RIGHT_KING_CASTLE_COLUMN) {
                        blackScore += CASTLE_VALUE;
                    }
                }
            }

            for (int index = (blacks.size() - 1); index != 0; --index) {
                Piece piece = blacks.get(index);
                blackScore += exposed(grid, piece);
                final int row = piece.getRow();
                final int column = piece.getColumn();
                //least valuable pieces first, since looping backwards
                if (piece.isPawn()) {
                    //blackScore += PawnEvaluator.evaluateBlackPawn(grid, row, column);
                    blackScore += PAWN_MOBILITY * piece.getNumberOfProtectedTiles(grid);
                    blackScore += PAWN_POSITION_BLACK[row * LENGTH + column];
                    ++blackPawns;
                }
                else if (piece.isKnight()) {
                    blackScore += KNIGHT_MOBILITY * piece.getNumberOfProtectedTiles(grid);
                    blackScore += KNIGHT_POSITION_BLACK[row * LENGTH + column];
                    ++blackKnights;
                }
                else if (piece.isBishop()) {
                    blackScore += BISHOP_MOBILITY * piece.getNumberOfProtectedTiles(grid);
                    blackScore += BISHOP_POSITION_BLACK[row * LENGTH + column];
                    ++blackBishops;
                }
                else if (piece.isRook()) {
                    blackScore += ROOK_MOBILITY * piece.getNumberOfProtectedTiles(grid);
                    blackScore += ROOK_POSITION_BLACK[row * LENGTH + column];
                    ++blackRooks;
                }
                else {
                    blackScore += QUEEN_MOBILITY * piece.getNumberOfProtectedTiles(grid);
                    blackScore += QUEEN_POSITION_BLACK[row * LENGTH + column];
                    ++blackQueens;
                }
            }

            final int materialScore = (blackQueens * QUEEN_VALUE) + (blackRooks * ROOK_VALUE) + (blackBishops * BISHOP_VALUE) + (blackKnights * KNIGHT_VALUE) + (blackPawns * PAWN_VALUE);

            if (materialScore <= 1200) {
                blackScore += KING_POSITION_BLACK_END[blackKingLocation];
                blackScore += KING_MOBILITY * blackKing.getNumberOfProtectedTiles(grid);
            }
            else {
                blackScore += KING_POSITION_BLACK_MID[blackKingLocation];
            }

            blackScore += (blackBishops >= 2) ? materialScore + BISHOP_BONUS : materialScore;
        }
        {
            //king must be first
            final Piece whiteKing = whites.get(0);
            final int whiteKingColumn = whiteKing.getColumn();
            final int whiteKingLocation = whiteKing.getRow() * LENGTH + whiteKingColumn;
            {
                int moveCount = whiteKing.getMoveCount();
                if (moveCount > 1) {
                    whiteScore -= KING_MOVED_VALUE;
                }
                if (grid.getTile(whiteKingLocation).protectedByBlack()) {
                    whiteScore -= CHECKED_VALUE;
                }
                else if (moveCount == 1) {
                    //only award castling bonus if king is not under siege
                    if (whiteKingColumn == LEFT_KING_CASTLE_COLUMN || whiteKingColumn == RIGHT_KING_CASTLE_COLUMN) {
                        whiteScore += CASTLE_VALUE;
                    }
                }
            }

            for (int index = (whites.size() - 1); index != 0; --index) {
                Piece piece = whites.get(index);
                whiteScore += exposed(grid, piece);
                final int row = piece.getRow();
                final int column = piece.getColumn();
                if (piece.isPawn()) {
                    //whiteScore += PawnEvaluator.evaluateWhitePawn(grid, row, column);
                    whiteScore += PAWN_MOBILITY * piece.getNumberOfProtectedTiles(grid);
                    whiteScore += PAWN_POSITION_WHITE[row * LENGTH + column];
                    ++whitePawns;
                }
                else if (piece.isKnight()) {
                    whiteScore += KNIGHT_MOBILITY * piece.getNumberOfProtectedTiles(grid);
                    whiteScore += KNIGHT_POSITION_WHITE[row * LENGTH + column];
                    ++whiteKnights;
                }
                else if (piece.isBishop()) {
                    whiteScore += BISHOP_MOBILITY * piece.getNumberOfProtectedTiles(grid);
                    whiteScore += BISHOP_POSITION_WHITE[row * LENGTH + column];
                    ++whiteBishops;
                }
                else if (piece.isRook()) {
                    whiteScore += ROOK_MOBILITY * piece.getNumberOfProtectedTiles(grid);
                    whiteScore += ROOK_POSITION_WHITE[row * LENGTH + column];
                    ++whiteRooks;
                }
                else {
                    whiteScore += QUEEN_MOBILITY * piece.getNumberOfProtectedTiles(grid);
                    whiteScore += QUEEN_POSITION_WHITE[row * LENGTH + column];
                    ++whiteQueens;
                }
            }

            final int materialScore = (whiteQueens * QUEEN_VALUE) + (whiteRooks * ROOK_VALUE) + (whiteBishops * BISHOP_VALUE) + (whiteKnights * KNIGHT_VALUE) + (whitePawns * PAWN_VALUE);

            if (materialScore <= 1200) {
                whiteScore += KING_POSITION_WHITE_END[whiteKingLocation];
                whiteScore += KING_MOBILITY * whiteKing.getNumberOfProtectedTiles(grid);
            }
            else {
                whiteScore += KING_POSITION_WHITE_MID[whiteKingLocation];
            }

            whiteScore += (whiteBishops >= 2) ? materialScore + BISHOP_BONUS : materialScore;
        }

        final int whitePawnsLost = 8 - whitePawns;
        final int blackPawnsLost = 8 - blackPawns;

        //black knights lose value as there is less white pawns
        blackScore -= (whitePawnsLost * 5) * blackKnights;
        //multiply by number of black knights to amplify the penalty accordingly

        //black rooks gain value as there is less white pawns
        blackScore += (whitePawnsLost * 5) * blackRooks;
        //multiply by number of black rooks to amplify bonus accordingly

        //white knights lose value as there is less black pawns
        whiteScore -= (blackPawnsLost * 5) * whiteKnights;
        //multiply by number of white knights to amplify the penalty accordingly

        //white rooks gain value as there is less black pawns
        whiteScore += (blackPawnsLost * 5) * whiteRooks;
        //multiply by number of white rooks to amplify bonus accordingly

        return blackScore - whiteScore;
    }

    private static int getBlackScoreUnsorted(Grid grid, List<Piece> blacks) {

        int blackScore = 0;
        int blackQueens = 0;
        int blackRooks = 0;
        int blackBishops = 0;
        int blackKnights = 0;
        int blackPawns = 0;

        //king must be first
        final Piece blackKing = blacks.get(0);
        final int blackKingColumn = blackKing.getColumn();
        final int blackKingLocation = blackKing.getRow() * LENGTH + blackKingColumn;

        {
            int moveCount = blackKing.getMoveCount();
            if (moveCount > 1) {
                blackScore -= KING_MOVED_VALUE;
            }
            if (grid.getTile(blackKingLocation).protectedByWhite()) {
                blackScore -= CHECKED_VALUE;
            }
            else if (moveCount == 1) {
                switch (blackKingColumn) {
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

        for (int index = (blacks.size() - 1); index != 0; --index) {
            Piece piece = blacks.get(index);
            blackScore += exposed(grid, piece);
            final int row = piece.getRow();
            final int column = piece.getColumn();
            //most common pieces first
            if (piece.isPawn()) {
                blackScore += PawnEvaluator.evaluateBlackPawn(grid, row, column);
                blackScore += PAWN_MOBILITY * piece.getNumberOfProtectedTiles(grid);
                blackScore += PAWN_POSITION_BLACK[row * LENGTH + column];
                ++blackPawns;
            }
            else if (piece.isQueen()) {
                blackScore += QUEEN_MOBILITY * piece.getNumberOfProtectedTiles(grid);
                blackScore += QUEEN_POSITION_BLACK[row * LENGTH + column];
                ++blackQueens;
            }
            else if (piece.isRook()) {
                blackScore += ROOK_MOBILITY * piece.getNumberOfProtectedTiles(grid);
                blackScore += ROOK_POSITION_BLACK[row * LENGTH + column];
                ++blackRooks;
            }
            else if (piece.isBishop()) {
                blackScore += BISHOP_MOBILITY * piece.getNumberOfProtectedTiles(grid);
                blackScore += BISHOP_POSITION_BLACK[row * LENGTH + column];
                ++blackBishops;
            }
            else if (piece.isKnight()) {
                //knights here
                blackScore += KNIGHT_MOBILITY * piece.getNumberOfProtectedTiles(grid);
                blackScore += KNIGHT_POSITION_BLACK[row * LENGTH + column];
                ++blackKnights;
            }
            else {
                throw new Error();
            }
        }

        int materialScore = (blackQueens * QUEEN_VALUE) + (blackRooks * ROOK_VALUE) + (blackBishops * BISHOP_VALUE) + (blackKnights * KNIGHT_VALUE) + (blackPawns * PAWN_VALUE);

        if (materialScore <= 1200) {
            blackScore += KING_POSITION_BLACK_END[blackKingLocation];
            blackScore += KING_MOBILITY * blackKing.getNumberOfProtectedTiles(grid);
            blackScore += (blackRooks * 15); //rooks worth more in endgame
            blackScore += (blackBishops * 15); //bishops worth more in endgame
            blackScore -= (blackKnights * 15); //knights worth less in endgame
        }
        else {
            blackScore += KING_POSITION_BLACK_MID[blackKingLocation];
        }

        return blackBishops >= 2
                ? blackScore + materialScore + BISHOP_BONUS
                : blackScore + materialScore;
    }

    private static int getWhiteScoreUnsorted(Grid grid, List<Piece> whites) {

        int whiteScore = 0;
        int whiteQueens = 0;
        int whiteRooks = 0;
        int whiteBishops = 0;
        int whiteKnights = 0;
        int whitePawns = 0;

        final Piece whiteKing = whites.get(0);
        final int whiteKingColumn = whiteKing.getColumn();
        final int whiteKingLocation = whiteKing.getRow() * LENGTH + whiteKingColumn;

        {
            int moveCount = whiteKing.getMoveCount();
            if (moveCount > 1) {
                whiteScore -= KING_MOVED_VALUE;
            }
            if (grid.getTile(whiteKingLocation).protectedByBlack()) {
                whiteScore -= CHECKED_VALUE;
            }
            else if (moveCount == 1) {
                switch (whiteKingColumn) {
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

        for (int index = (whites.size() - 1); index != 0; --index) {
            Piece piece = whites.get(index);
            whiteScore += exposed(grid, piece);
            final int row = piece.getRow();
            final int column = piece.getColumn();
            if (piece.isPawn()) {
                whiteScore += PawnEvaluator.evaluateWhitePawn(grid, row, column);
                whiteScore += PAWN_MOBILITY * piece.getNumberOfProtectedTiles(grid);
                whiteScore += PAWN_POSITION_WHITE[row * LENGTH + column];
                ++whitePawns;
            }
            else if (piece.isQueen()) {
                whiteScore += QUEEN_MOBILITY * piece.getNumberOfProtectedTiles(grid);
                whiteScore += QUEEN_POSITION_WHITE[row * LENGTH + column];
                ++whiteQueens;
            }
            else if (piece.isRook()) {
                whiteScore += ROOK_MOBILITY * piece.getNumberOfProtectedTiles(grid);
                whiteScore += ROOK_POSITION_WHITE[row * LENGTH + column];
                ++whiteRooks;
            }
            else if (piece.isBishop()) {
                whiteScore += BISHOP_MOBILITY * piece.getNumberOfProtectedTiles(grid);
                whiteScore += BISHOP_POSITION_WHITE[row * LENGTH + column];
                ++whiteBishops;
            }
            else if (piece.isKnight()) {
                whiteScore += KNIGHT_MOBILITY * piece.getNumberOfProtectedTiles(grid);
                whiteScore += KNIGHT_POSITION_WHITE[row * LENGTH + column];
                ++whiteKnights;
            }
            else {
                throw new Error();
            }
        }

        int materialScore = (whiteQueens * QUEEN_VALUE) + (whiteRooks * ROOK_VALUE) + (whiteBishops * BISHOP_VALUE) + (whiteKnights * KNIGHT_VALUE) + (whitePawns * PAWN_VALUE);

        if (materialScore <= 1200) {
            whiteScore += KING_POSITION_WHITE_END[whiteKingLocation];
            whiteScore += KING_MOBILITY * whiteKing.getNumberOfProtectedTiles(grid);
            whiteScore += (whiteRooks * 15); //rooks worth more in endgame
            whiteScore += (whiteBishops * 15); //bishops worth more in endgame
            whiteScore -= (whiteKnights * 15); //knights worth less in endgame
        }
        else {
            whiteScore += KING_POSITION_WHITE_MID[whiteKingLocation];
        }

        return whiteBishops >= 2
                ? whiteScore + materialScore + BISHOP_BONUS
                : whiteScore + materialScore;
    }
}