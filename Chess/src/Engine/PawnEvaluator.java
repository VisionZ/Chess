package Engine;

import static Util.ChessConstants.BLACK_PAWN_START_ROW;
import static Util.ChessConstants.WHITE_PAWN_START_ROW;

/**
 * Evaluates Pawns, giving a bonus, penalty or 0 depending on the context of the
 * board.
 *
 * @author zwill
 */
public final class PawnEvaluator {

    private PawnEvaluator() {

    }

    private static final int LENGTH = 8;
    
    private static final int DOUBLED_PAWN_PENALTY = 20; //severe penalty if 2 or more pawns are in one vertical column
    private static final int ISOLATED_PAWN_PENALTY = 15; //less penalty, pawn can be protected by other pieces

    /**
     * White seeks to move pawns from row 6 to row 1. When White Pawns reach row
     * 1, they are one step away from being promoted. Note that it is impossible
     * for any White Pawn to be on row 0 since it would've already been
     * promoted.
     */
    private static final int[] PASSED_PAWN_BONUS_WHITE = {
        0,  //row 0, White Pawn already promoted
        60, //row 1, White Pawn about to be promoted
        45, //row 2
        30, //row 3
        20, //row 4
        10, //row 5
        0   //row 6, White Pawn starting row 
    };

    /**
     * Black seeks to move pawns from row 1 to row 6. When Black Pawns reach row
     * 6, they are one step away from being promoted. Note that it is impossible
     * for any Black Pawn to be on row 7 since it would've already been
     * promoted.
     */
    private static final int[] PASSED_PAWN_BONUS_BLACK = {
        0,  //row 0, Black Pieces
        0,  //row 1, Black Pawn starting row
        10, //row 2
        20, //row 3
        30, //row 4
        45, //row 5
        60, //row 6, Black Pawn about to be promoted
    };

    public static final int evaluateWhitePawn(final Grid grid, final int whitePawnRow, final int whitePawnColumn) {
        final int leftColumn = whitePawnColumn - 1;
        final int rightColumn = whitePawnColumn + 1;

        int score = 0;

        boolean passedPawn = true;

        //on the direct vertical column 
        //we check to see if this pawn is a doubled pawn or a passed pawn
        for (int row = whitePawnRow - 1; row >= BLACK_PAWN_START_ROW; --row) {
            Piece piece = grid.getTile(row, whitePawnColumn).getOccupant();
            if (piece != null) {
                if (piece.isPawn()) {
                    //doubled pawn is where allied pawn is directly in front of this one
                    if (piece.isWhite()) {
                        score -= DOUBLED_PAWN_PENALTY; 
                    }
                    else {
                        //if there is an enemy pawn directly in front of this one
                        //then this pawn is not a passed pawn
                        passedPawn = false;
                    }
                }
            }
        }

        //on the left adjacent vertical column
        //we check to see if this pawn is a isolated or a passed pawn
        if (leftColumn >= 0) {
            boolean leftPawnAlly = false;
            for (int row = WHITE_PAWN_START_ROW; row >= BLACK_PAWN_START_ROW; --row) {
                Piece piece = grid.getTile(row, leftColumn).getOccupant();
                if (piece != null) {
                    if (piece.isPawn()) {
                        //if there is an allied pawn to the left (in front or behind), then this pawn is not isolated
                        if (piece.isWhite()) {
                            leftPawnAlly = true;
                        }
                        else {
                            //if there is an enemy pawn in front of this one on the left
                            //then this pawn is not a passed pawn
                            passedPawn = false;
                        }
                    }
                }
            }
            if (!leftPawnAlly) {
                score -= ISOLATED_PAWN_PENALTY;
            }
        }

        //on the right adjacent vertical column
        //we check to see if this pawn is a isolated or a passed pawn
        if (rightColumn < LENGTH) {
            boolean rightPawnAlly = false;
            for (int row = WHITE_PAWN_START_ROW; row >= BLACK_PAWN_START_ROW; --row) {
                Piece piece = grid.getTile(row, rightColumn).getOccupant();
                if (piece != null) {
                    if (piece.isPawn()) {
                        //if there is an allied pawn to the right (in front or behind), then this pawn is not isolated
                        if (piece.isWhite()) {
                            rightPawnAlly = true;
                        }
                        else {
                            //if there is an enemy pawn in front of this one on the right
                            //then this pawn is not a passed pawn
                            passedPawn = false;
                        }
                    }
                }
            }
            if (!rightPawnAlly) {
                score -= ISOLATED_PAWN_PENALTY;
            }
        }

        return !passedPawn ? score : score + PASSED_PAWN_BONUS_WHITE[whitePawnRow];
    }

    public static final int evaluateBlackPawn(final Grid grid, final int blackPawnRow, final int blackPawnColumn) {
        final int leftColumn = blackPawnColumn - 1;
        final int rightColumn = blackPawnColumn + 1;

        int score = 0;

        boolean passedPawn = true;

        //on the direct vertical column 
        //we check to see if this pawn is a doubled pawn or a passed pawn
        for (int row = blackPawnRow + 1; row <= WHITE_PAWN_START_ROW; ++row) {
            Piece piece = grid.getTile(row, blackPawnColumn).getOccupant();
            if (piece != null) {
                if (piece.isPawn()) {
                    //doubled pawn is where allied pawn is directly in front of this one
                    if (piece.isBlack()) {
                        score -= DOUBLED_PAWN_PENALTY;
                    }
                    else {
                        //if there is an enemy pawn directly in front of this one
                        //then this pawn is not a passed pawn
                        passedPawn = false;
                    }
                }
            }
        }

        //on the left adjacent vertical column
        //we check to see if this pawn is a isolated or a passed pawn
        if (leftColumn >= 0) {
            boolean leftPawnAlly = false;
            for (int row = BLACK_PAWN_START_ROW; row <= WHITE_PAWN_START_ROW; ++row) {
                Piece piece = grid.getTile(row, leftColumn).getOccupant();
                if (piece != null) {
                    if (piece.isPawn()) {
                        //if there is an allied pawn to the left (in front or behind), then this pawn is not isolated
                        if (piece.isBlack()) {
                            leftPawnAlly = true;
                        }
                        else {
                            //if there is an enemy pawn in front of this one on the left
                            //then this pawn is not a passed pawn
                            passedPawn = false;
                        }
                    }
                }
            }
            if (!leftPawnAlly) {
                score -= ISOLATED_PAWN_PENALTY;
            }
        }

        //on the right adjacent vertical column
        //we check to see if this pawn is a isolated or a passed pawn
        if (rightColumn < LENGTH) {
            boolean rightPawnAlly = false;
            for (int row = BLACK_PAWN_START_ROW; row <= WHITE_PAWN_START_ROW; ++row) {
                Piece piece = grid.getTile(row, rightColumn).getOccupant();
                if (piece != null) {
                    if (piece.isPawn()) {
                        //if there is an allied pawn to the right (in front or behind), then this pawn is not isolated
                        if (piece.isBlack()) {
                            rightPawnAlly = true;
                        }
                        else {
                            //if there is an enemy pawn in front of this one on the right
                            //then this pawn is not a passed pawn
                            passedPawn = false;
                        }
                    }
                }
            }
            if (!rightPawnAlly) {
                score -= ISOLATED_PAWN_PENALTY;
            }
        }

        return !passedPawn ? score : score + PASSED_PAWN_BONUS_BLACK[blackPawnRow];
    }
}