package FastAI;

import Util.ChessConstants;
import static Util.ChessConstants.BISHOP_PREFIX;
import static Util.ChessConstants.BLACK_PREFIX;
import static Util.ChessConstants.KING_PREFIX;
import static Util.ChessConstants.KNIGHT_PREFIX;
import static Util.ChessConstants.LENGTH;
import static Util.ChessConstants.LINEAR_LENGTH;
import static Util.ChessConstants.PAWN_PREFIX;
import static Util.ChessConstants.QUEEN_PREFIX;
import static Util.ChessConstants.ROOK_PREFIX;
import static Util.ChessConstants.WHITE_PREFIX;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class Board {
    
    public static final BitBoard getEnemyProtections(Board board, boolean color) {
        return color ? board.blackProtections : board.whiteProtections;
    }
    
    

    public static final long L = 1L;

    final BitBoard pieces;

    final BitBoard whitePawns;
    final BitBoard whiteKnights;
    final BitBoard whiteBishops;
    final BitBoard whiteRooks;
    final BitBoard whiteQueens;
    final BitBoard whiteKings;

    final BitBoard whitePieces;

    final BitBoard blackPawns;
    final BitBoard blackKnights;
    final BitBoard blackBishops;
    final BitBoard blackRooks;
    final BitBoard blackQueens;
    final BitBoard blackKings;

    final BitBoard blackPieces; //positions of black pieces on chess board.

    final int[] moveCounts;

    //at any given time, there will be only one pawn that has just performed a double jump
    //regardless of color
    final BitBoard pawnsDoubleJumped = new BitBoard(0L);

    final BitBoard whiteProtections; //must intialize manually
    final BitBoard blackProtections; //must initialize manually

    public Board() {
        whitePawns =       new BitBoard(0b00000000_00000000_00000000_00000000_00000000_00000000_11111111_00000000L);
        whiteKnights =     new BitBoard(0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_01000010L);
        whiteBishops =     new BitBoard(0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00100100L);
        whiteRooks =       new BitBoard(0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_10000001L);
        whiteQueens =      new BitBoard(0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00010000L);
        whiteKings =       new BitBoard(0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00001000L);
    
        //to loop from     [0][0] to [7][7] start at 63 and stop at 0
        //could hard co    de white pieces, but this initialization code shows the process
        blackPawns =       new BitBoard(0b00000000_11111111_00000000_00000000_00000000_00000000_00000000_00000000L);
        blackKnights =     new BitBoard(0b01000010_00000000_00000000_00000000_00000000_00000000_00000000_00000000L);
        blackBishops =     new BitBoard(0b00100100_00000000_00000000_00000000_00000000_00000000_00000000_00000000L);
        blackRooks =       new BitBoard(0b10000001_00000000_00000000_00000000_00000000_00000000_00000000_00000000L);
        blackQueens =      new BitBoard(0b00010000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L);
        blackKings =       new BitBoard(0b00001000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L);

        whitePieces =      new BitBoard(whitePawns.getBits() | whiteKnights.getBits() | whiteBishops.getBits() | whiteRooks.getBits() | whiteQueens.getBits() | whiteKings.getBits());
        blackPieces =      new BitBoard(blackPawns.getBits() | blackKnights.getBits() | blackBishops.getBits() | blackRooks.getBits() | blackQueens.getBits() | blackKings.getBits());
        pieces =           new BitBoard(whitePieces.getBits() | blackPieces.getBits());
        
        moveCounts = new int[LINEAR_LENGTH];
        
        whiteProtections = new BitBoard(0b00000000_00000000_00000000_00000000_00000000_11111111_11111111_11111111L);
        blackProtections = new BitBoard(0b11111111_11111111_11111111_00000000_00000000_00000000_00000000_00000000L);
    }

    public Board(Board grid) {
        whitePawns = grid.whitePawns.clone();
        whiteKnights = grid.whiteKnights.clone();
        whiteBishops = grid.whiteBishops.clone();
        whiteRooks = grid.whiteRooks.clone();
        whiteQueens = grid.whiteQueens.clone();
        whiteKings = grid.whiteKings.clone();

        blackPawns = grid.blackPawns.clone();
        blackKnights = grid.blackKnights.clone();
        blackBishops = grid.blackBishops.clone();
        blackRooks = grid.blackRooks.clone();
        blackQueens = grid.blackQueens.clone();
        blackKings = grid.blackKings.clone();
        
        whitePieces = grid.whitePieces.clone();
        blackPieces = grid.blackPieces.clone();
        pieces = grid.pieces.clone();
        
        moveCounts = Arrays.copyOf(grid.moveCounts, LINEAR_LENGTH);
        
        whiteProtections = grid.whiteProtections.clone();
        blackProtections = grid.blackProtections.clone();
    }

    @Override
    public String toString() {
        //fills from top left to bottom right 
        String[][] board = new String[LENGTH][LENGTH];
        for (int row = 0, column = 0, index = LINEAR_LENGTH - 1; index >= 0; --index) {
            if (column == LENGTH) {
                ++row;
                column = 0;
            }
            long shift = L << index;
            if ((whitePawns.getBits() & shift) != 0) {
                board[row][column] = WHITE_PREFIX + PAWN_PREFIX;
            }
            else if ((whiteKnights.getBits() & shift) != 0) {
                board[row][column] = WHITE_PREFIX + KNIGHT_PREFIX;
            }
            else if ((whiteBishops.getBits() & shift) != 0) {
                board[row][column] = WHITE_PREFIX + BISHOP_PREFIX;
            }
            else if ((whiteRooks.getBits() & shift) != 0) {
                board[row][column] = WHITE_PREFIX + ROOK_PREFIX;
            }
            else if ((whiteQueens.getBits() & shift) != 0) {
                board[row][column] = WHITE_PREFIX + QUEEN_PREFIX;
            }
            else if ((whiteKings.getBits() & shift) != 0) {
                board[row][column] = WHITE_PREFIX + KING_PREFIX;
            }

            else if ((blackPawns.getBits() & shift) != 0) {
                board[row][column] = BLACK_PREFIX + PAWN_PREFIX;
            }
            else if ((blackKnights.getBits() & shift) != 0) {
                board[row][column] = BLACK_PREFIX + KNIGHT_PREFIX;
            }
            else if ((blackBishops.getBits() & shift) != 0) {
                board[row][column] = BLACK_PREFIX + BISHOP_PREFIX;
            }
            else if ((blackRooks.getBits() & shift) != 0) {
                board[row][column] = BLACK_PREFIX + ROOK_PREFIX;
            }
            else if ((blackQueens.getBits() & shift) != 0) {
                board[row][column] = BLACK_PREFIX + QUEEN_PREFIX;
            }
            else if ((blackKings.getBits() & shift) != 0) {
                board[row][column] = BLACK_PREFIX + KING_PREFIX;
            }
            else {
                //board[row][column] = "  ";
                board[row][column] = Integer.toString(ChessConstants.getLocation(row, column));
            }
            ++column;
        }
        
        StringBuilder result = new StringBuilder(LINEAR_LENGTH);
        
        for (int index = 0; index < LENGTH;) {
            result.append(Arrays.toString(board[index]));
            if (++index == LENGTH) {
                break;
            }
            result.append("\n");
        }
        
        return result.toString();
    }

    public static String toBitBoardString(long l) {
        String result = Long.toBinaryString(l);
        
        //LINEAR_LENGTH will always be bigger
        if (LINEAR_LENGTH != result.length()) {
            for (int diff = LINEAR_LENGTH - result.length(); diff > 0; --diff) {
                result = "0" + result;
            }
        }

        return result.substring(0, 8)
                + "\n"
                + result.substring(8, 16)
                + "\n"
                + result.substring(16, 24)
                + "\n"
                + result.substring(24, 32)
                + "\n"
                + result.substring(32, 40)
                + "\n"
                + result.substring(40, 48)
                + "\n"
                + result.substring(48, 56)
                + "\n"
                + result.substring(56);
    }

    //determines wheather the nth bit is set
    public static final boolean isBitSet(long board, int n) {
        return (board & (L << n)) != 0;
    }
    
    //allows for traditional board indexing
    //[0][0] to [7][7] where [0][0] is the upper left hand corner and [7][7] is the 
    //bottom right hand corner
    public static final boolean isBitSetStandard(long board, int n) {
        return (board & (L << (63 - n))) != 0;
    }

    public static void main(String[] args) {
        Board board = new Board();
        System.out.println();
        System.out.println(board.hashCode());
        System.out.println("Pieces:\n" + toBitBoardString(board.pieces.getBits()));
        System.out.println();
        System.out.println("Whites:\n" + toBitBoardString(board.whitePieces.getBits()));
        System.out.println();
        System.out.println("Blacks:\n" + toBitBoardString(board.blackPieces.getBits()));
        System.out.println();
        System.out.println(board);
        System.out.println(isBitSetStandard(board.blackBishops.getBits(), 61)); //should be false for black bishops, true for white
        System.out.println(isBitSetStandard(board.blackBishops.getBits(), 58)); //should be false for black bishops, true for white
        System.out.println("Bishop at 27");
        for (int each : Bishop.getMoveTiles(board, 27)) { //assumes bishop is at position 27
            System.out.println(each + " " + ChessConstants.getLocation(each));
        }
        System.out.println("White King at 19");
        for (int each : King.getMoveTiles(board, 19, true)) { //assumes bishop is at position 27
            System.out.println(each + " " + ChessConstants.getLocation(each));
        }
        System.out.println("White Bishop at 19");
        for (int each : Bishop.getCaptureTiles(board, 19, true)) {
            System.out.println(each + " " + ChessConstants.getLocation(each));
        }
        System.out.println("White Bishop at 19");
        System.out.println(board.whiteProtections.toBitString());
        System.out.println();
        Bishop.setProtectedTiles(board, 19, true);
        Bishop.setProtectedTiles(board, 19, true);
        System.out.println(board.whiteProtections.toBitString());

        System.out.println(parseDouble("-2000000.3234523523523523"));
        System.out.println(parseDouble("2000000.323523523523532535"));

        //location of bits
        {
            for (long b = board.pieces.getBits(); b != 0;) {
                long c = b & (-b);
                int index = Long.numberOfTrailingZeros(c);
                //Do something...
                System.out.println(index);
                b = b ^ c;
            }
        }
    }

    public static double parseDouble(String str) {
        if (str.startsWith("-")) {
            str = str.substring(1);
            int decimal = str.indexOf(".");
            if (decimal < 0) {
                return Integer.parseInt(str);
            }
            double result = 0.0;
            int units = 0;
            for (int index = decimal - 1; index >= 0; --index, ++units) {
                result += (1.0 * Integer.parseInt(str.substring(index, index + 1))) * pow(10, units);
            }
            units = -1;
            for (int index = decimal + 1, len = str.length(); index != len; ++index, --units) {
                result += (1.0 * Integer.parseInt(str.substring(index, index + 1))) * pow(10, units);
            }
            return -result;
        }
        else {
            int decimal = str.indexOf(".");
            if (decimal < 0) {
                return Integer.parseInt(str);
            }
            double result = 0.0;
            int units = 0;
            for (int index = decimal - 1; index >= 0; --index, ++units) {
                result += (1.0 * Integer.parseInt(str.substring(index, index + 1))) * pow(10, units);
            }
            units = -1;
            for (int index = decimal + 1, len = str.length(); index != len; ++index, --units) {
                result += (1.0 * Integer.parseInt(str.substring(index, index + 1))) * pow(10, units);
            }
            return result;
        }
    }
    
    private static final Map<Point2D.Double, Double> HIST = new HashMap<>();

    public static double pow(int value, int times) {
        Point2D.Double entry = new Point2D.Double(value, times);
        if (HIST.containsKey(entry)) {
            System.out.println("Read");
            return HIST.get(entry);
        }
        double result = 1.0;
        if (times < 0) {
            for (times = -times; times > 0; --times) {
                result *= value;
            }
            HIST.put(entry, result = 1.0 / result);
            return result;
        }
        for (; times > 0; --times) {
            result *= value;
        }
        HIST.put(entry, result);
        return result;
    }
}
