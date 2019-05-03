package FastAI;

import static Util.ChessConstants.BLACK_PIECE_ROW;
import static Util.ChessConstants.LEFT_KING_CASTLE_COLUMN;
import static Util.ChessConstants.LEFT_ROOK_START_COLUMN;
import static Util.ChessConstants.LENGTH;
import static Util.ChessConstants.NUMBER_OF_KING_PROTECTED_TILES;
import static Util.ChessConstants.RIGHT_KING_CASTLE_COLUMN;
import static Util.ChessConstants.RIGHT_ROOK_START_COLUMN;
import static Util.ChessConstants.WHITE_PIECE_ROW;
import static Util.ChessConstants.getColumn;
import static Util.ChessConstants.getLocation;
import static Util.ChessConstants.getRow;
import java.util.ArrayList;
import java.util.List;

public class King {

    public static final List<Integer> getMoveTiles(final Board board, final int index, final boolean color) {
        final BitBoard enemyProtections = color ? board.blackProtections : board.whiteProtections;

        final List<Integer> list = new ArrayList<>(NUMBER_OF_KING_PROTECTED_TILES);

        final int currentRow = getRow(index);
        final int currentColumn = getColumn(index);
        final int up = currentRow - 1;
        final int down = currentRow + 1;
        final int left = currentColumn - 1;
        final int right = currentColumn + 1;

        if (up >= 0) {
            if (left >= 0) {
                int tile = getLocation(up, left);
                if (!enemyProtections.isBitSet(tile) && !board.pieces.isBitSet(tile)) {
                    list.add(tile);
                }
            }
            int tile = getLocation(up, currentColumn);
            if (!enemyProtections.isBitSet(tile) && !board.pieces.isBitSet(tile)) {
                list.add(tile);
            }
            if (right < LENGTH) {
                tile = getLocation(up, right);
                if (!enemyProtections.isBitSet(tile) && !board.pieces.isBitSet(tile)) {
                    list.add(tile);
                }
            }
        }
        if (left >= 0) {
            int tile = getLocation(currentRow, left);
            if (!enemyProtections.isBitSet(tile) && !board.pieces.isBitSet(tile)) {
                list.add(tile);
            }
        }
        if (right < LENGTH) {
            int tile = getLocation(currentRow, right);
            if (!enemyProtections.isBitSet(tile) && !board.pieces.isBitSet(tile)) {
                list.add(tile);
            }
        }
        if (down < LENGTH) {
            if (left >= 0) {
                int tile = getLocation(down, left);
                if (!enemyProtections.isBitSet(tile) && !board.pieces.isBitSet(tile)) {
                    list.add(tile);
                }
            }
            int tile = getLocation(down, currentColumn);
            if (!enemyProtections.isBitSet(tile) && !board.pieces.isBitSet(tile)) {
                list.add(tile);
            }
            if (right < LENGTH) {
                tile = getLocation(down, right);
                if (!enemyProtections.isBitSet(tile) && !board.pieces.isBitSet(tile)) {
                    list.add(tile);
                }
            }
        }
        return list;
    }

    public static final List<Integer> getAttackTiles(final Board board, final int index, final boolean color) {
        final BitBoard enemyProtections;
        final BitBoard enemyPieces;

        if (color) {
            enemyProtections = board.blackProtections;
            enemyPieces = board.blackPieces;
        }
        else {
            enemyProtections = board.whiteProtections;
            enemyPieces = board.whitePieces;
        }
        
        final List<Integer> list = new ArrayList<>(NUMBER_OF_KING_PROTECTED_TILES);

        final int currentRow = getRow(index);
        final int currentColumn = getColumn(index);
        final int up = currentRow - 1;
        final int down = currentRow + 1;
        final int left = currentColumn - 1;
        final int right = currentColumn + 1;

        if (up >= 0) {
            if (left >= 0) {
                int tile = getLocation(up, left);
                if (!enemyProtections.isBitSet(tile) && enemyPieces.isBitSet(tile)) {
                    list.add(tile);
                }
            }
            int tile = getLocation(up, currentColumn);
            if (!enemyProtections.isBitSet(tile) && enemyPieces.isBitSet(tile)) {
                list.add(tile);
            }
            if (right < LENGTH) {
                tile = getLocation(up, right);
                if (!enemyProtections.isBitSet(tile) && enemyPieces.isBitSet(tile)) {
                    list.add(tile);
                }
            }
        }
        if (left >= 0) {
            int tile = getLocation(currentRow, left);
            if (!enemyProtections.isBitSet(tile) && enemyPieces.isBitSet(tile)) {
                list.add(tile);
            }
        }
        if (right < LENGTH) {
            int tile = getLocation(currentRow, right);
            if (!enemyProtections.isBitSet(tile) && enemyPieces.isBitSet(tile)) {
                list.add(tile);
            }
        }
        if (down < LENGTH) {
            if (left >= 0) {
                int tile = getLocation(down, left);
                if (!enemyProtections.isBitSet(tile) && enemyPieces.isBitSet(tile)) {
                    list.add(tile);
                }
            }
            int tile = getLocation(down, currentColumn);
            if (!enemyProtections.isBitSet(tile) && enemyPieces.isBitSet(tile)) {
                list.add(tile);
            }
            if (right < LENGTH) {
                tile = getLocation(down, right);
                if (!enemyProtections.isBitSet(tile) && enemyPieces.isBitSet(tile)) {
                    list.add(tile);
                }
            }
        }
        return list;
    }
    
    public static final boolean inCheck(final Board board, final int index, final boolean color) {
        return color ? board.blackProtections.isBitSet(index) : board.whiteProtections.isBitSet(index);
    }

    public static final boolean canCastle(final Board board, final int index, final boolean color) {
        //configured to call the least number of methods as possible
        if (board.moveCounts[index] != 0) {
            return false;
        }
        return color
                ? (!board.blackProtections.isBitSet(index) && getRow(index) == WHITE_PIECE_ROW) 
                : (!board.whiteProtections.isBitSet(index) && getRow(index) == BLACK_PIECE_ROW);
    }

    public static final int getLeftCastleTile(final Board board, final int index, final boolean color) {
        final int currentRow = getRow(index);
        final int leftRookLocation = getLocation(currentRow, LEFT_ROOK_START_COLUMN);

        if (color) {
            if (board.whiteRooks.isBitSet(leftRookLocation) && board.moveCounts[leftRookLocation] == 0) {
                for (int column = 1; column < 4; ++column) {
                    int path = getLocation(currentRow, column);
                    if (board.blackProtections.isBitSet(path) || board.pieces.isBitSet(path)) {
                        return -1;
                    }
                }
                return getLocation(currentRow, LEFT_KING_CASTLE_COLUMN);
            }
        }
        else {
            if (board.blackRooks.isBitSet(leftRookLocation) && board.moveCounts[leftRookLocation] == 0) {
                for (int column = 1; column < 4; ++column) {
                    int path = getLocation(currentRow, column);
                    if (board.whiteProtections.isBitSet(path) || board.pieces.isBitSet(path)) {
                        return -1;
                    }
                }
                return getLocation(currentRow, LEFT_KING_CASTLE_COLUMN);
            }
        }
        return -1;
    }
    
    public static final int getRightCastleTile(final Board board, final int index, final boolean color) {
        final int currentRow = getRow(index);
        final int rightRookLocation = getLocation(currentRow, RIGHT_ROOK_START_COLUMN);

        if (color) {
            if (board.whiteRooks.isBitSet(rightRookLocation) && board.moveCounts[rightRookLocation] == 0) {
                for (int column = 5; column < 7; ++column) {
                    int path = getLocation(currentRow, column);
                    if (board.blackProtections.isBitSet(path) || board.pieces.isBitSet(path)) {
                        return -1;
                    }
                }
                return getLocation(currentRow, RIGHT_KING_CASTLE_COLUMN);
            }
        }
        else {
            if (board.blackRooks.isBitSet(rightRookLocation) && board.moveCounts[rightRookLocation] == 0) {
                for (int column = 5; column < 7; ++column) {
                    int path = getLocation(currentRow, column);
                    if (board.whiteProtections.isBitSet(path) || board.pieces.isBitSet(path)) {
                        return -1;
                    }
                }
                return getLocation(currentRow, RIGHT_KING_CASTLE_COLUMN);
            }
        }
        return -1;
    }

    public static final void setProtectedTiles(final Board board, final int index, final boolean color) {
        final BitBoard protections = color ? board.whiteProtections : board.blackProtections;

        final int currentRow = getRow(index);
        final int currentColumn = getColumn(index);
        final int up = currentRow - 1;
        final int down = currentRow + 1;
        final int left = currentColumn - 1;
        final int right = currentColumn + 1;

        if (up >= 0) {
            if (left >= 0) {
                int tile = getLocation(up, left);
                protections.setBit(tile);
            }
            int tile = getLocation(up, currentColumn);
            protections.setBit(tile);
            if (right < LENGTH) {
                tile = getLocation(up, right);
                protections.setBit(tile);
            }
        }
        if (left >= 0) {
            int tile = getLocation(currentRow, left);
            protections.setBit(tile);
        }
        if (right < LENGTH) {
            int tile = getLocation(currentRow, right);
            protections.setBit(tile);
        }
        if (down < LENGTH) {
            if (left >= 0) {
                int tile = getLocation(down, left);
                protections.setBit(tile);
            }
            int tile = getLocation(down, currentColumn);
            protections.setBit(tile);
            if (right < LENGTH) {
                tile = getLocation(down, right);
                protections.setBit(tile);
            }
        }
    }
}