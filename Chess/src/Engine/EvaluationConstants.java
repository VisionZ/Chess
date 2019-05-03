package Engine;

import static Util.ChessConstants.BISHOP_VALUE;
import static Util.ChessConstants.KNIGHT_VALUE;
import static Util.ChessConstants.PAWN_VALUE;
import static Util.ChessConstants.QUEEN_VALUE;
import static Util.ChessConstants.ROOK_VALUE;

public final class EvaluationConstants {
    
    public static final int PAWN_MOBILITY = 6; //pawns that can't move forward due to direct blockage right in front of them are bad
    public static final int KNIGHT_MOBILITY = 8; //knights need to move more tiles, not less
    public static final int BISHOP_MOBILITY = 8; //bishops as well
    public static final int ROOK_MOBILITY = 5;
    public static final int QUEEN_MOBILITY = 3;
    public static final int KING_MOBILITY = 10;

    private static final int VALUE_FACTOR = 2;
     //how much is it worth to guard an ally or enemy
    //depending on piece type
    public static final int PAWN_PROTECTION = PAWN_VALUE / VALUE_FACTOR;
    public static final int KNIGHT_PROTECTION = KNIGHT_VALUE / VALUE_FACTOR;
    public static final int BISHOP_PROTECTION = BISHOP_VALUE / VALUE_FACTOR;
    public static final int ROOK_PROTECTION = ROOK_VALUE / VALUE_FACTOR;
    public static final int QUEEN_PROTECTION = QUEEN_VALUE / VALUE_FACTOR;

    //protect the tiles around the king, not the tile the king is on
    public static final int KING_PROTECTION = 100;

    public static final int BISHOP_BONUS = 30;
    public static final int CASTLE_VALUE = 20;
    public static final int CHECKED_VALUE = 10;
    public static final int KING_MOVED_VALUE = 15;

    public static final int CHECKMATE_VALUE = 200000000;

    private EvaluationConstants() {

    }
}