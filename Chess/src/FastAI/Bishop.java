package FastAI;

import static Util.ChessConstants.LENGTH;
import static Util.ChessConstants.NUMBER_OF_ROOK_AND_BISHOP_ATTACK_TILES;
import static Util.ChessConstants.getColumn;
import static Util.ChessConstants.getLocation;
import static Util.ChessConstants.getRow;
import java.util.ArrayList;
import java.util.List;

public final class Bishop {

    private Bishop() {

    }

    //allows for traditional board indexing
    //[0][0] to [7][7] where [0][0] is the upper left hand corner and [7][7] is the 
    //bottom right hand corner
    public static final List<Integer> getMoveTiles(final Board board, final int index) {
        List<Integer> list = new ArrayList<>();
        final int currentRow = getRow(index);
        final int currentColumn = getColumn(index);
        for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0; --nextRow, --nextColumn) {
            int tile = getLocation(nextRow, nextColumn);
            //check if the tileIndex bit is set for any piece
            if (board.pieces.isBitSet(tile)) {
                break;
            }
            list.add(tile);
        }
        for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH; --nextRow, ++nextColumn) {
            int tile = getLocation(nextRow, nextColumn);
            //check if the tileIndex bit is set for any piece
            if (board.pieces.isBitSet(tile)) {
                break;
            }
            list.add(tile);
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0; ++nextRow, --nextColumn) {
            int tile = getLocation(nextRow, nextColumn);
            //check if the tileIndex bit is set for any piece
            if (board.pieces.isBitSet(tile)) {
                break;
            }
            list.add(tile);
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH; ++nextRow, ++nextColumn) {
            int tile = getLocation(nextRow, nextColumn);
            //check if the tileIndex bit is set for any piece
            if (board.pieces.isBitSet(tile)) {
                break;
            }
            list.add(tile);
        }
        return list;
    }
    
    public static final List<Integer> getCaptureTiles(final Board board, final int index, final boolean color) {
        final BitBoard enemyPieces = color ? board.blackPieces : board.whitePieces;
        final List<Integer> list = new ArrayList<>(NUMBER_OF_ROOK_AND_BISHOP_ATTACK_TILES);
        final int currentRow = getRow(index);
        final int currentColumn = getColumn(index);
        for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0; --nextRow, --nextColumn) {
            int tile = getLocation(nextRow, nextColumn);
            if (board.pieces.isBitSet(tile)) {
                if (enemyPieces.isBitSet(tile)) {
                    list.add(tile);
                }
                break;
            }
        }
        for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH; --nextRow, ++nextColumn) {
            int tile = getLocation(nextRow, nextColumn);
            if (board.pieces.isBitSet(tile)) {
                if (enemyPieces.isBitSet(tile)) {
                    list.add(tile);
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0; ++nextRow, --nextColumn) {
            int tile = getLocation(nextRow, nextColumn);
            if (board.pieces.isBitSet(tile)) {
                if (enemyPieces.isBitSet(tile)) {
                    list.add(tile);
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH; ++nextRow, ++nextColumn) {
            int tile = getLocation(nextRow, nextColumn);
            if (board.pieces.isBitSet(tile)) {
                if (enemyPieces.isBitSet(tile)) {
                    list.add(tile);
                }
                break;
            }
        }
        return list;
    }
    
    public static final void setProtectedTiles(final Board board, final int index, final boolean color) {
        final BitBoard protections;
        final BitBoard enemyKing;
        
        if (color) {
            protections = board.whiteProtections;
            enemyKing = board.blackKings;
        }
        else {
            protections = board.blackProtections;
            enemyKing = board.whiteKings;
        }
        
        final int currentRow = getRow(index);
        final int currentColumn = getColumn(index);
        
        for (int nextRow = currentRow - 1, nextColumn = currentColumn - 1; nextRow >= 0 && nextColumn >= 0; --nextRow, --nextColumn) {
            int tile = getLocation(nextRow, nextColumn);
            protections.setBit(tile);
            if (board.pieces.isBitSet(tile)) {
                if (enemyKing.isBitSet(tile)) {
                    if (--nextRow >= 0 && --nextColumn >= 0) {
                        int pierceTile = getLocation(nextRow, nextColumn);
                        if (!board.pieces.isBitSet(pierceTile)) {
                            protections.setBit(pierceTile);
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow - 1, nextColumn = currentColumn + 1; nextRow >= 0 && nextColumn < LENGTH; --nextRow, ++nextColumn) {            
            int tile = getLocation(nextRow, nextColumn);
            protections.setBit(tile);
            if (board.pieces.isBitSet(tile)) {
                if (enemyKing.isBitSet(tile)) {
                    if (--nextRow >= 0 && ++nextColumn < LENGTH) {
                        int pierceTile = getLocation(nextRow, nextColumn);
                        if (!board.pieces.isBitSet(pierceTile)) {
                            protections.setBit(pierceTile);
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn - 1; nextRow < LENGTH && nextColumn >= 0; ++nextRow, --nextColumn) {
            int tile = getLocation(nextRow, nextColumn);
            protections.setBit(tile);
            if (board.pieces.isBitSet(tile)) {
                if (enemyKing.isBitSet(tile)) {
                    if (++nextRow < LENGTH && --nextColumn >= 0) {
                        int pierceTile = getLocation(nextRow, nextColumn);
                        if (!board.pieces.isBitSet(pierceTile)) {
                            protections.setBit(pierceTile);
                        }
                    }
                }
                break;
            }
        }
        for (int nextRow = currentRow + 1, nextColumn = currentColumn + 1; nextRow < LENGTH && nextColumn < LENGTH; ++nextRow, ++nextColumn) {
            int tile = getLocation(nextRow, nextColumn);
            protections.setBit(tile);
            if (board.pieces.isBitSet(tile)) {
                if (enemyKing.isBitSet(tile)) {
                    if (++nextRow < LENGTH && ++nextColumn < LENGTH) {
                        int pierceTile = getLocation(nextRow, nextColumn);
                        if (!board.pieces.isBitSet(pierceTile)) {
                            protections.setBit(pierceTile);
                        }
                    }
                }
                break;
            }
        }
    }
}