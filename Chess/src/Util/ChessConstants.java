package Util;

import java.awt.Point;

public final class ChessConstants {
    
    //number of initial positions from beginning
    public static final int INITAL_NUMBER_OF_POSITIONS = 20;

    //grid dimensions
    public static final int LENGTH = 8;
    public static final int LINEAR_LENGTH = 64;

    //number of pieces
    public static final int NUMBER_OF_PIECES = 32;
    public static final int NUMBER_OF_PIECES_PER_SIDE = 16;

    //game states
    public static final int SAFE = 0;
    public static final int CHECKED = 1;
    public static final int CHECKMATED = 2;
    public static final int STALEMATED = 3;

    //colors
    public static final String WHITE = "White";
    public static final String BLACK = "Black";

    //piece types
    public static final String PAWN = "Pawn";
    public static final String KNIGHT = "Knight";
    public static final String BISHOP = "Bishop";
    public static final String ROOK = "Rook";
    public static final String QUEEN = "Queen";
    public static final String KING = "King";
    
    public static final String WHITE_PREFIX = "W";
    public static final String BLACK_PREFIX = "B";
    
    public static final String PAWN_PREFIX = "P";
    public static final String KNIGHT_PREFIX = "N";
    public static final String BISHOP_PREFIX = "B";
    public static final String ROOK_PREFIX = "R";
    public static final String QUEEN_PREFIX = "Q";
    public static final String KING_PREFIX = "K";

    //white piece symbols
    public static final char WHITE_PAWN = '♙';
    public static final char WHITE_KNIGHT = '♘';
    public static final char WHITE_BISHOP = '♗';
    public static final char WHITE_ROOK = '♖';
    public static final char WHITE_QUEEN = '♕';
    public static final char WHITE_KING = '♔';
    
    //black piece symbols
    public static final char BLACK_PAWN = '♟';
    public static final char BLACK_KNIGHT = '♞';
    public static final char BLACK_BISHOP = '♝';
    public static final char BLACK_ROOK = '♜';
    public static final char BLACK_QUEEN = '♛';
    public static final char BLACK_KING = '♚';

    //locations
    public static final int WHITE_PIECE_ROW = 7;
    public static final int WHITE_PAWN_START_ROW = 6;
    public static final int BLACK_PIECE_ROW = 0;
    public static final int BLACK_PAWN_START_ROW = 1;
    
    //castling
    public static final int KING_START_COLUMN = 4;
    public static final int LEFT_ROOK_START_COLUMN = 0;
    public static final int RIGHT_ROOK_START_COLUMN = 7;
    public static final int LEFT_KING_CASTLE_COLUMN = 2;
    public static final int RIGHT_KING_CASTLE_COLUMN = 6;
    public static final int LEFT_ROOK_CASTLE_COLUMN = 3;
    public static final int RIGHT_ROOK_CASTLE_COLUMN = 5;
    
    //enpassant
    public static final int WHITE_ENPASSANT_ROW = 3;
    public static final int BLACK_ENPASSANT_ROW = 4;
    
    public static final int WHITE_PAWN_DOUBLE_JUMP_ROW = 4;
    public static final int BLACK_PAWN_DOUBLE_JUMP_ROW = 3;

    //memory allocations
    public static final int NUMBER_OF_PAWN_PROTECTED_TILES = 2;
    public static final int NUMBER_OF_KNIGHT_PROTECTED_TILES = 8;
    public static final int NUMBER_OF_ROOK_AND_BISHOP_ATTACK_TILES = 4;
    public static final int NUMBER_OF_QUEEN_ATTACK_TILES = 8;
    public static final int NUMBER_OF_KING_PROTECTED_TILES = 8;
    public static final int NUMBER_OF_CASTLE_TILES = 2;
    public static final int NUMBER_OF_BISHOP_MOVE_TILES = 13;
    public static final int NUMBER_OF_ROOK_MOVE_TILES = 14;
    public static final int NUMBER_OF_QUEEN_MOVE_TILES = 27;

    //piece value constants
    public static final int PAWN_VALUE = 100;
    public static final int KNIGHT_VALUE = 300;
    public static final int BISHOP_VALUE = 320;
    public static final int ROOK_VALUE = 500;
    public static final int QUEEN_VALUE = 920;
    public static final int KING_VALUE = 500000;

    private ChessConstants() {

    }
    
    //conversion methods between [8][8] and [64]
    //indexes [0][0] -> [0] & [7][7] -> [63]
    public static final int getLocation(int row, int column) {
        return LENGTH * row + column;
    }
    
    public static final Point getLocation(int index) {
        return new Point(index / LENGTH, index % LENGTH);
    }
    
    public static final int getRow(int index) {
        return index / LENGTH;
    }
    
    public static final int getColumn(int index) {
        return index % LENGTH;
    }

    public static void main(String[] args) {
        for (int index = 0; index < LINEAR_LENGTH; ++index) {
            Point point = getLocation(index);
            System.out.println("Current Linear Index: " + getLocation(point.x, point.y));
            System.out.println("Current Matrix Index: [" + point.x + "][" + point.y + "]");
            System.out.println(getRow(index) + " " + getColumn(index));
            if (getRow(index) != point.x || getColumn(index) != point.y  || getLocation(point.x, point.y) != index) {
                throw new Error();
            }
            System.out.println();
        }
    }
}