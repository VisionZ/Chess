package FastAI;

import static Util.ChessConstants.BLACK_ENPASSANT_ROW;
import static Util.ChessConstants.getColumn;
import static Util.ChessConstants.getLocation;
import static Util.ChessConstants.getRow;
import static Util.ChessConstants.LENGTH;
import static Util.ChessConstants.NUMBER_OF_PAWN_PROTECTED_TILES;
import static Util.ChessConstants.WHITE_ENPASSANT_ROW;
import java.util.ArrayList;
import java.util.List;

public final class Pawn {

    public static final List<Integer> getMoveTiles(final Board board, final int index, final boolean color) {
        final List<Integer> list = new ArrayList<>(NUMBER_OF_PAWN_PROTECTED_TILES);
        if (!color) {
            final int nextRow = getRow(index) + 1;
            if (board.moveCounts[index] == 0) {
                final int currentColumn = getColumn(index);
                final int firstTileDown = getLocation(nextRow, currentColumn);
                if (!board.pieces.isBitSet(firstTileDown)) {
                    list.add(firstTileDown);
                    final int secondTileDown = getLocation(nextRow + 1, currentColumn);
                    if (!board.pieces.isBitSet(secondTileDown)) {
                        list.add(secondTileDown);
                    }
                }
                return list;
            }
            if (nextRow < LENGTH) {
                final int firstTileDown = getLocation(nextRow, getColumn(index));
                if (!board.pieces.isBitSet(firstTileDown)) {
                    list.add(firstTileDown);
                }
            }
            return list;
        }
        final int nextRow = getRow(index) - 1;
        if (board.moveCounts[index] == 0) {
            final int currentColumn = getColumn(index);
            final int firstTileUp = getLocation(nextRow, currentColumn);
            if (!board.pieces.isBitSet(firstTileUp)) {
                list.add(firstTileUp);
                final int secondTileUp = getLocation(nextRow - 1, currentColumn);
                if (!board.pieces.isBitSet(secondTileUp)) {
                    list.add(secondTileUp);
                }
            }
            return list;
        }
        if (nextRow >= 0) {
            final int firstTileUp = getLocation(nextRow, getColumn(index));
            if (!board.pieces.isBitSet(firstTileUp)) {
                list.add(firstTileUp);
            }
        }
        return list;
    }
    
    public static final List<Integer> getAttackTiles(final Board board, final int index, final boolean color) {
        final List<Integer> list = new ArrayList<>(NUMBER_OF_PAWN_PROTECTED_TILES);
        if (!color) {
            final int nextRow = getRow(index) + 1;
            if (nextRow < LENGTH) {
                final int currentColumn = getColumn(index);
                int nextColumn = currentColumn - 1;
                if (nextColumn >= 0) {
                    final int tile = getLocation(nextRow, nextColumn);
                    if (board.whitePieces.isBitSet(tile)) {
                        list.add(tile);
                    }
                }
                if ((nextColumn = currentColumn + 1) < LENGTH) {
                    final int tile = getLocation(nextRow, nextColumn);
                    if (board.whitePieces.isBitSet(tile)) {
                        list.add(tile);
                    }
                }
            }
            return list;
        }
        final int nextRow = getRow(index) - 1;
        if (nextRow >= 0) {
            final int currentColumn = getColumn(index);
            int nextColumn = currentColumn - 1;
            if (nextColumn >= 0) {
                final int tile = getLocation(nextRow, nextColumn);
                if (board.blackPieces.isBitSet(tile)) {
                    list.add(tile);
                }
            }
            if ((nextColumn = currentColumn + 1) < LENGTH) {
                final int tile = getLocation(nextRow, nextColumn);
                if (board.blackPieces.isBitSet(tile)) {
                    list.add(tile);
                }
            }
        }
        return list;
    }
    
    public static final int getLeftEnPassantTile(final Board board, final int index, final boolean color) {
        final int currentRow = getRow(index);
        if (color) {
            if (currentRow != WHITE_ENPASSANT_ROW) {
                return -1;
            }
            final int leftColumn = getColumn(index) - 1;
            if (leftColumn >= 0) {
                final int blackPawnLocation = getLocation(currentRow, leftColumn);
                if (board.pawnsDoubleJumped.isBitSet(blackPawnLocation) && board.blackPawns.isBitSet(blackPawnLocation)) {
                    final int leftEnPassantTile = getLocation(currentRow - 1, leftColumn);
                    if (!board.pieces.isBitSet(leftEnPassantTile)) {
                        return leftEnPassantTile;
                    }
                }
            }
        }
        else {
            if (currentRow != BLACK_ENPASSANT_ROW) {
                return -1;
            }
            final int leftColumn = getColumn(index) - 1;
            if (leftColumn >= 0) {
                final int whitePawnLocation = getLocation(currentRow, leftColumn);
                if (board.pawnsDoubleJumped.isBitSet(whitePawnLocation) && board.whitePawns.isBitSet(whitePawnLocation)) {
                    final int leftEnPassantTile = getLocation(currentRow + 1, leftColumn);
                    if (!board.pieces.isBitSet(leftEnPassantTile)) {
                        return leftEnPassantTile;
                    }
                }
            }
        }
        return -1;
    }
    
    public static final int getRightEnPassantTile(final Board board, final int index, final boolean color) {
        final int currentRow = getRow(index);
        if (color) {
            if (currentRow != WHITE_ENPASSANT_ROW) {
                return -1;
            }
            final int rightColumn = getColumn(index) + 1;
            if (rightColumn < LENGTH) {
                final int blackPawnLocation = getLocation(currentRow, rightColumn);
                if (board.pawnsDoubleJumped.isBitSet(blackPawnLocation) && board.blackPawns.isBitSet(blackPawnLocation)) {
                    final int rightEnPassantTile = getLocation(currentRow - 1, rightColumn);
                    if (!board.pieces.isBitSet(rightEnPassantTile)) {
                        return rightEnPassantTile;
                    }
                }
            }
        }
        else {
            if (currentRow != BLACK_ENPASSANT_ROW) {
                return -1;
            }
            final int rightColumn = getColumn(index) + 1;
            if (rightColumn < LENGTH) {
                final int whitePawnLocation = getLocation(currentRow, rightColumn);
                if (board.pawnsDoubleJumped.isBitSet(whitePawnLocation) && board.whitePawns.isBitSet(whitePawnLocation)) {
                    final int rightEnPassantTile = getLocation(currentRow + 1, rightColumn);
                    if (!board.pieces.isBitSet(rightEnPassantTile)) {
                        return rightEnPassantTile;
                    }
                }
            }
        }
        return -1;
    }
    
}
