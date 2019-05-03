package FastAI;

import static Util.ChessConstants.LENGTH;
import static Util.ChessConstants.NUMBER_OF_KNIGHT_PROTECTED_TILES;
import static Util.ChessConstants.getColumn;
import static Util.ChessConstants.getLocation;
import static Util.ChessConstants.getRow;
import java.util.ArrayList;
import java.util.List;

public final class Knight {
    //allows for traditional board indexing
    //[0][0] to [7][7] where [0][0] is the upper left hand corner and [7][7] is the 
    //bottom right hand corner
    public static final List<Integer> getMoveTiles(final Board board, final int index) {
        final List<Integer> list = new ArrayList<>(NUMBER_OF_KNIGHT_PROTECTED_TILES);
        
        final int currentRow = getRow(index);
        final int currentColumn = getColumn(index);

        final int up = currentRow - 1;
        final int down = currentRow + 1;
        final int left = currentColumn - 1;
        final int right = currentColumn + 1;

        final int up2 = currentRow - 2;
        final int down2 = currentRow + 2;
        final int left2 = currentColumn - 2;
        final int right2 = currentColumn + 2;

        if (up >= 0) {
            if (left2 >= 0) {
                int tile = getLocation(up, left2);
                if (!board.pieces.isBitSet(index)) {
                    list.add(tile);
                }
            }
            if (right2 < LENGTH) {
                int tile = getLocation(up, right2);
                if (!board.pieces.isBitSet(index)) {
                    list.add(tile);
                }
            }
        }
        if (down < LENGTH) {
            if (left2 >= 0) {
                int tile = getLocation(down, left2);
                if (!board.pieces.isBitSet(index)) {
                    list.add(tile);
                }
            }
            if (right2 < LENGTH) {
                int tile = getLocation(down, right2);
                if (!board.pieces.isBitSet(index)) {
                    list.add(tile);
                }
            }
        }
        if (left >= 0) {
            if (up2 >= 0) {
                int tile = getLocation(up2, left);
                if (!board.pieces.isBitSet(index)) {
                    list.add(tile);
                }
            }
            if (down2 < LENGTH) {
                int tile = getLocation(down2, left);
                if (!board.pieces.isBitSet(index)) {
                    list.add(tile);
                }
            }
        }
        if (right < LENGTH) {
            if (up2 >= 0) {
                int tile = getLocation(up2, right);
                if (!board.pieces.isBitSet(index)) {
                    list.add(tile);
                }
            }
            if (down2 < LENGTH) {
                int tile = getLocation(down2, right);
                if (!board.pieces.isBitSet(index)) {
                    list.add(tile);
                }
            }
        }
        
        return list;
    }
    
    public static final List<Integer> getCaptureTiles(final Board board, final int index, final boolean color) {
        final BitBoard enemies = color ? board.blackPieces : board.whitePieces;
        final List<Integer> list = new ArrayList<>(NUMBER_OF_KNIGHT_PROTECTED_TILES);
        
        final int currentRow = getRow(index);
        final int currentColumn = getColumn(index);

        final int up = currentRow - 1;
        final int down = currentRow + 1;
        final int left = currentColumn - 1;
        final int right = currentColumn + 1;

        final int up2 = currentRow - 2;
        final int down2 = currentRow + 2;
        final int left2 = currentColumn - 2;
        final int right2 = currentColumn + 2;

        if (up >= 0) {
            if (left2 >= 0) {
                int tile = getLocation(up, left2);
                if (enemies.isBitSet(tile)) {
                    list.add(tile);
                }
            }
            if (right2 < LENGTH) {
                int tile = getLocation(up, right2);
                if (enemies.isBitSet(tile)) {
                    list.add(tile);
                }
            }
        }
        if (down < LENGTH) {
            if (left2 >= 0) {
                int tile = getLocation(down, left2);
                if (enemies.isBitSet(tile)) {
                    list.add(tile);
                }
            }
            if (right2 < LENGTH) {
                int tile = getLocation(down, right2);
                if (enemies.isBitSet(tile)) {
                    list.add(tile);
                }
            }
        }
        if (left >= 0) {
            if (up2 >= 0) {
                int tile = getLocation(up2, left);
                if (enemies.isBitSet(tile)) {
                    list.add(tile);
                }
            }
            if (down2 < LENGTH) {
                int tile = getLocation(down2, left);
                if (enemies.isBitSet(tile)) {
                    list.add(tile);
                }
            }
        }
        if (right < LENGTH) {
            if (up2 >= 0) {
                int tile = getLocation(up2, right);
                if (enemies.isBitSet(tile)) {
                    list.add(tile);
                }
            }
            if (down2 < LENGTH) {
                int tile = getLocation(down2, right);
                if (enemies.isBitSet(tile)) {
                    list.add(tile);
                }
            }
        }
        
        return list;
    }
    
    public static final void setProtectedTiles(final Board board, final int index, final boolean color) {
        final BitBoard protections = color ? board.whiteProtections : board.blackProtections;
               
        final int currentRow = getRow(index);
        final int currentColumn = getColumn(index);

        final int up = currentRow - 1;
        final int down = currentRow + 1;
        final int left = currentColumn - 1;
        final int right = currentColumn + 1;

        final int up2 = currentRow - 2;
        final int down2 = currentRow + 2;
        final int left2 = currentColumn - 2;
        final int right2 = currentColumn + 2;

        if (up >= 0) {
            if (left2 >= 0) {
                int tile = getLocation(up, left2);
                protections.setBit(tile);
            }
            if (right2 < LENGTH) {
                int tile = getLocation(up, right2);
                protections.setBit(tile);
            }
        }
        if (down < LENGTH) {
            if (left2 >= 0) {
                int tile = getLocation(down, left2);
                protections.setBit(tile);
            }
            if (right2 < LENGTH) {
                int tile = getLocation(down, right2);
                protections.setBit(tile);
            }
        }
        if (left >= 0) {
            if (up2 >= 0) {
                int tile = getLocation(up2, left);
                protections.setBit(tile);
            }
            if (down2 < LENGTH) {
                int tile = getLocation(down2, left);
                protections.setBit(tile);
            }
        }
        if (right < LENGTH) {
            if (up2 >= 0) {
                int tile = getLocation(up2, right);
                protections.setBit(tile);
            }
            if (down2 < LENGTH) {
                int tile = getLocation(down2, right);
                protections.setBit(tile);
            }
        }
    }
}
